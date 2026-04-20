package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyStatusNotifierService
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.domain.queue.exceptions.QueueLeaveException
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.domain.queue.services.QueueTimerService
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.exceptions.TournamentParticipantEnterException
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.exceptions.TournamentParticipantReadyException
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter.TournamentParticipantEnter
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playEnterQueueSound
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import net.serverwars.sunsetPlugin.util.runAsync
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

object TournamentParticipantService {

    private const val COOLDOWN = 50L

    fun ready(audience: Audience): CompletableFuture<Unit> {
        if (QueueService.cooldownTaskId != null) return CompletableFuture.completedFuture(Unit)

        audience.sendTranslatedMessage("command.tournament.readying")
        return runAsync {
            try {
                TournamentParticipantDataAccess.readyServer()
                addCooldown()
                audience.sendTranslatedMessage("command.tournament.ready.success")
            } catch (error: TournamentParticipantReadyException) {
                audience.sendTranslatedMessage(error.key, *error.args)
            }
        }
    }

    fun enter(audience: Audience): CompletableFuture<Unit> {
        if (QueueService.cooldownTaskId != null) return CompletableFuture.completedFuture(Unit)

        audience.sendTranslatedMessage("command.tournament.entering")
        return runAsync {
            try {
                sendEnterTournament()
                addCooldown()
            } catch (error: TournamentParticipantEnterException) {
                audience.sendTranslatedMessage(error.key, *error.args)
            }
        }
    }

    fun leave(audience: Audience = Audience.empty()): CompletableFuture<Unit> {
        if (QueueService.cooldownTaskId != null) return CompletableFuture.completedFuture(Unit)

        return runAsync {
            try {
                QueueService.leaveQueue(audience)
                addCooldown()
            } catch(error: QueueLeaveException) {
                audience.sendTranslatedMessage(error.key, *error.args)
            }
        }
    }

    private suspend fun sendEnterTournament() {
        if (QueueService.queueUuid != null) {
            throw TournamentParticipantEnterException("command.queue.enter.error.already_in_queue")
        }

        val lobby = LobbyService.getLobbyCopy() ?: throw TournamentParticipantEnterException("command.queue.enter.error.no_lobby")

        if (lobby.getParticipantAmount() < Lobby.MIN_LOBBY_SIZE) {
            throw TournamentParticipantEnterException(
                "command.queue.enter.error.not_enough_players",
                lobby.getParticipantAmount(),
                Lobby.MIN_LOBBY_SIZE
            )
        }

        // Make sure server is not in match yet
        try {
            val isInMatch = MatchService.checkInMatch()
            if (isInMatch) {
                throw TournamentParticipantEnterException("command.queue.enter.error.already_in_match")
            }
        } catch (_: ApiException) {
            throw TournamentParticipantEnterException("command.queue.enter.error.api_exception")
        }

        // Enter queue
        val tournamentParticipantEnter = TournamentParticipantEnter(
            queueEntryCreate = QueueEntryCreateMapper.fromLobby(lobby),
        )
        try {
            val queueEntryCreateResponse = TournamentParticipantDataAccess.enterServer(tournamentParticipantEnter)
            QueueService.queueUuid = queueEntryCreateResponse.queueEntryUuid
            QueueTimerService.startTimer(lobby, queueEntryCreateResponse.queueEntryUuid)
        } catch (_: ApiException) {
            throw TournamentParticipantEnterException("command.queue.enter.error.api_exception")
        }
        lobby.sendMessage("command.queue.enter.success.notify_lobby")
        playEnterQueueSound(lobby)
        LobbyStatusNotifierService.stopShowingLobbyStatus()

        QueueService.queueEnterTimestamp = now()
        Main.inst.logger.info("[QUEUE] Entering tournament, waiting for opponent...")
    }

    private fun addCooldown() {
        QueueService.cooldownTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.inst, Runnable {
            QueueService.cooldownTaskId = null
        }, this.COOLDOWN).taskId
    }
}