package net.serverwars.sunsetPlugin.domain.queue.services

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyStatusNotifierService
import net.serverwars.sunsetPlugin.domain.match.services.MatchDataAccess
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.QueueEnterMenuItem
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.QueueLeaveMenuItem
import net.serverwars.sunsetPlugin.domain.queue.exceptions.QueueEnterException
import net.serverwars.sunsetPlugin.domain.queue.exceptions.QueueLeaveException
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatus
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusType
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playEnterQueueSound
import net.serverwars.sunsetPlugin.util.playLeaveQueueSound
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import net.serverwars.sunsetPlugin.util.runAsync
import net.serverwars.sunsetPlugin.util.runSync
import org.bukkit.Bukkit
import org.bukkit.Material
import java.util.*
import java.util.concurrent.CompletableFuture

object QueueService {

    const val QUEUE_COOLDOWN_AFTER_ENTER = 30L
    const val QUEUE_COOLDOWN_AFTER_LEAVE = 80L // Equal to queue update interval

    var queueEnterTimestamp: Long? = null
    var queueUuid: UUID? = null
    var cooldownTaskId: Int? = null

    fun getTimeInQueue(): Long = this.queueEnterTimestamp?.let { now() - it } ?: 0

    fun enterQueue(audience: Audience): CompletableFuture<Unit> {
        if (this.cooldownTaskId != null) return CompletableFuture.completedFuture(Unit)

        audience.sendTranslatedMessage("command.queue.entering")
        return runAsync {
            try {
                sendEnterQueue()
                addCooldown(this.QUEUE_COOLDOWN_AFTER_ENTER, QueueLeaveMenuItem.material)
            } catch (error: QueueEnterException) {
                audience.sendTranslatedMessage(error.key, *error.args)
            }
        }
    }

    fun leaveQueue(audience: Audience = Audience.empty()): CompletableFuture<Unit> {
        if (this.cooldownTaskId != null) return CompletableFuture.completedFuture(Unit)

        audience.sendTranslatedMessage("command.queue.leaving")
        return runAsync {
            try {
                sendLeaveQueue()
                addCooldown(this.QUEUE_COOLDOWN_AFTER_LEAVE, QueueEnterMenuItem.material)
            } catch(error: QueueLeaveException) {
                audience.sendTranslatedMessage(error.key, *error.args)
            }
        }
    }

    private suspend fun sendEnterQueue() {
        if (this.queueUuid != null) {
            throw QueueEnterException("command.queue.enter.error.already_in_queue")
        }

        val lobby = LobbyService.getLobbyCopy() ?: throw QueueEnterException("command.queue.enter.error.no_lobby")

        if (lobby.getParticipantAmount() < Lobby.MIN_LOBBY_SIZE) {
            throw QueueEnterException(
                "command.queue.enter.error.not_enough_players",
                lobby.getParticipantAmount(),
                Lobby.MIN_LOBBY_SIZE
            )
        }

        // Make sure server is not in match yet
        try {
            val isInMatch = MatchService.checkInMatch()
            if (isInMatch) {
                throw QueueEnterException("command.queue.enter.error.already_in_match")
            }
        } catch (_: ApiException) {
            throw QueueEnterException("command.queue.enter.error.api_exception")
        }

        // Enter queue
        val queueEntryCreate = QueueEntryCreateMapper.fromLobby(lobby)
        try {
            val queueEntryCreateResponse = QueueDataAccess.enterQueue(queueEntryCreate)
            this.queueUuid = queueEntryCreateResponse.queueEntryUuid
            QueueTimerService.startTimer(lobby, queueEntryCreateResponse.queueEntryUuid)
        } catch (_: ApiException) {
            throw QueueEnterException("command.queue.enter.error.api_exception")
        }
        lobby.sendMessage("command.queue.enter.success.notify_lobby")
        playEnterQueueSound(lobby)
        LobbyStatusNotifierService.stopShowingLobbyStatus()

        this.queueEnterTimestamp = now()
        Main.inst.logger.info("[QUEUE] Entering queue, searching for opponents...")
    }

    private suspend fun sendLeaveQueue() {
        val queueUuidCopy = this.queueUuid ?: throw QueueLeaveException("command.queue.leave.error.not_in_queue")

        this.queueUuid = null
        QueueTimerService.stopTimer(queueUuidCopy)

        try {
            QueueDataAccess.leaveQueue(queueUuidCopy)

            LobbyService.getLobbyCopy()?.let {
                it.sendMessage("command.queue.leave.success.notify_lobby")
                playLeaveQueueSound(it)
                LobbyStatusNotifierService.startShowingLobbyStatus()
            }
            Main.inst.logger.info("[QUEUE] Left queue, time in queue: ${getTimeInQueue()}ms")
            this.queueEnterTimestamp = null
        } catch (_: ApiException) {
            throw QueueLeaveException("command.queue.leave.error.api_exception")
        }
    }

    suspend fun parseQueueEntryStatus(queueEntryStatus: QueueEntryStatus) {
        if (queueEntryStatus.status == QueueEntryStatusType.MATCH_FOUND) {
            runSync {
                LobbyMenu.closeForAll()
            }

            val queueUuidCopy = this.queueUuid!!
            this.queueUuid = null
            this.queueEnterTimestamp = null

            QueueTimerService.stopTimer(queueUuidCopy)?.notifyMatchFound()

            val matchUuid = queueEntryStatus.matchUuid!!
            MatchService.setMatch(matchUuid)
            MatchDataAccess.listenToMatchStatusEvents(matchUuid = matchUuid)
        }
        else if (queueEntryStatus.status == QueueEntryStatusType.LEFT_QUEUE && this.queueUuid != null) {
            LobbyService.getLobbyCopy()?.sendMessage("command.queue.leave.error.forced_to_leave_queue")
            try {
                sendLeaveQueue()
            } catch (_: QueueLeaveException) { }
        }
    }

    fun isInQueue(): Boolean = this.queueUuid != null

    private fun addCooldown(cooldownInTicks: Long, material: Material) {
        this.cooldownTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.inst, Runnable {
            this.cooldownTaskId = null
        }, cooldownInTicks).taskId
        runSync {
            LobbyMenu.visualizeCooldownForAll(material, cooldownInTicks.toInt())
        }
    }

}