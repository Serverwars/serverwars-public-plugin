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
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatus
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusType
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playEnterQueueSound
import net.serverwars.sunsetPlugin.util.playLeaveQueueSound
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import net.serverwars.sunsetPlugin.util.rest.exceptions.ErrorCodeMessage
import net.serverwars.sunsetPlugin.util.rest.exceptions.callApi
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
        val default = CompletableFuture.completedFuture(Unit)

        // Check cooldown
        if (this.cooldownTaskId != null) {
            return default
        }

        // Check not in queue yet
        if (this.queueUuid != null) {
            audience.sendTranslatedMessage("command.queue.enter.error.already_in_queue")
            return default
        }

        // Check lobby exists
        val lobby = LobbyService.getLobbyCopy()
        if (lobby == null) {
            audience.sendTranslatedMessage("command.queue.enter.error.no_lobby")
            return default
        }

        // Check minimum amount of players in lobby
        if (lobby.getParticipantAmount() < Lobby.MIN_LOBBY_SIZE) {
            audience.sendTranslatedMessage(
                "command.queue.enter.error.not_enough_players",
                lobby.getParticipantAmount(),
                Lobby.MIN_LOBBY_SIZE
            )
            return default
        }

        audience.sendTranslatedMessage("command.queue.entering")
        return callApi(
            audience = audience,
            errorCodeMap = mapOf(
                "QUEUE_ENTRY_VALIDATION_ALREADY_IN_MATCH" to ErrorCodeMessage("command.queue.enter.error.already_in_match"),
            )
        ) {
            // Make sure server is not in match yet
            val isInMatch = MatchService.checkInMatch()
            if (isInMatch) {
                audience.sendTranslatedMessage(
                    "command.queue.enter.error.already_in_match",
                    lobby.getParticipantAmount(),
                    Lobby.MIN_LOBBY_SIZE
                )
                return@callApi
            }

            // Enter queue
            val queueEntryCreate = QueueEntryCreateMapper.fromLobby(lobby)
            val queueEntryCreateResponse = QueueDataAccess.enterQueue(queueEntryCreate)
            this.queueUuid = queueEntryCreateResponse.queueEntryUuid
            QueueTimerService.startTimer(lobby, queueEntryCreateResponse.queueEntryUuid)

            // Notify lobby
            lobby.sendMessage("command.queue.enter.success.notify_lobby")
            playEnterQueueSound(lobby)
            LobbyStatusNotifierService.stopShowingLobbyStatus()

            this.queueEnterTimestamp = now()
            Main.inst.logger.info("[QUEUE] Entering queue, searching for opponents...")
            addCooldown(this.QUEUE_COOLDOWN_AFTER_ENTER, QueueLeaveMenuItem.material)
        }
    }

    fun leaveQueue(audience: Audience = Audience.empty()): CompletableFuture<Unit> {
        val default = CompletableFuture.completedFuture(Unit)

        // Check cooldown
        if (this.cooldownTaskId != null) {
            return default
        }

        // Check in queue
        val queueUuidCopy = this.queueUuid
        if (queueUuidCopy == null) {
            audience.sendTranslatedMessage("command.queue.leave.error.not_in_queue")
            return default
        }

        // Remove queue uuid
        this.queueUuid = null
        QueueTimerService.stopTimer(queueUuidCopy)

        audience.sendTranslatedMessage("command.queue.leaving")
        return callApi(
            audience = audience,
            errorCodeMap = mapOf(
                "QUEUE_ENTRY_NOT_IN_QUEUE" to ErrorCodeMessage("command.queue.leave.error.not_in_queue"),
            )
        ) {
            // Leave queue
            QueueDataAccess.leaveQueue(queueUuidCopy)

            // Notify lobby
            LobbyService.getLobbyCopy()?.let {
                it.sendMessage("command.queue.leave.success.notify_lobby")
                playLeaveQueueSound(it)
                LobbyStatusNotifierService.startShowingLobbyStatus()
            }

            Main.inst.logger.info("[QUEUE] Left queue, time in queue: ${getTimeInQueue()}ms")
            this.queueEnterTimestamp = null
            addCooldown(this.QUEUE_COOLDOWN_AFTER_LEAVE, QueueEnterMenuItem.material)
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
        } else if (queueEntryStatus.status == QueueEntryStatusType.LEFT_QUEUE && this.queueUuid != null) {
            LobbyService.getLobbyCopy()?.sendMessage("command.queue.leave.error.forced_to_leave_queue")
            try {
                leaveQueue()
            } catch (_: ApiException) {
            }
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