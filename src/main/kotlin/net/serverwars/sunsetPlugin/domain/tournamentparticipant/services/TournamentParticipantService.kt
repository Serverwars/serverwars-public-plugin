package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyStatusNotifierService
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.domain.queue.services.QueueTimerService
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReady
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.now
import net.serverwars.sunsetPlugin.util.playEnterQueueSound
import net.serverwars.sunsetPlugin.util.rest.exceptions.ErrorCodeMessage
import net.serverwars.sunsetPlugin.util.rest.exceptions.callApi
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

object TournamentParticipantService {

    private const val COOLDOWN = 50L

    fun ready(audience: Audience): CompletableFuture<Unit> {
        val default = CompletableFuture.completedFuture(Unit)

        // Check cooldown
        if (QueueService.cooldownTaskId != null) {
            return default
        }

        // Check not in queue yet
        if (QueueService.queueUuid != null) {
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

        audience.sendTranslatedMessage("command.tournament.readying")

        return callApi(
            audience = audience,
            errorCodeMap = mapOf(
                "SERVER_SECRET_SECRET_NOT_FOUND" to ErrorCodeMessage("command.error.invalid_server_secret"),
                "TOURNAMENT_PARTICIPANT_VALIDATION_READY_ALREADY_READY" to ErrorCodeMessage("command.tournament.ready.error.already_ready"),
                "TOURNAMENT_PARTICIPANT_VALIDATION_READY_NOT_IN_READY_TOURNAMENT" to ErrorCodeMessage("command.tournament.ready.error.no_tournament"),
                "TOURNAMENT_PARTICIPANT_VALIDATION_READY_INVALID_GAME_TYPE" to ErrorCodeMessage("command.tournament.ready.error.invalid_game_type"),
                "TOURNAMENT_PARTICIPANT_VALIDATION_READY_INVALID_TEAM_SIZE" to ErrorCodeMessage("command.tournament.ready.error.invalid_team_size"),
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
            val tournamentParticipantEnter = TournamentParticipantReady(QueueEntryCreateMapper.fromLobby(lobby))
            val queueEntryCreateResponse = TournamentParticipantDataAccess.readyServer(tournamentParticipantEnter)
            QueueService.queueUuid = queueEntryCreateResponse.queueEntryUuid

            // Notify lobby
            QueueTimerService.startTimer(lobby, queueEntryCreateResponse.queueEntryUuid, "queue.tournament_waiting.action_bar")
            lobby.sendMessage("command.tournament.ready.success")
            playEnterQueueSound(lobby)
            LobbyStatusNotifierService.stopShowingLobbyStatus()

            QueueService.queueEnterTimestamp = now()
            Main.inst.logger.info("[QUEUE] Server marked ready, waiting for tournament to start...")
            addCooldown()
        }
    }

    fun unready(audience: Audience): CompletableFuture<Unit> {
        return QueueService.leaveQueue(audience)
    }


    private fun addCooldown() {
        QueueService.cooldownTaskId = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.inst, Runnable {
            QueueService.cooldownTaskId = null
        }, this.COOLDOWN).taskId
    }
}