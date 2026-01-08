package net.serverwars.sunsetPlugin.domain.queue.services

import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.domain.queue.exceptions.EnterQueueException
import net.serverwars.sunsetPlugin.domain.queue.exceptions.LeaveQueueException
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatus
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusType
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.util.playEnterQueueSound
import net.serverwars.sunsetPlugin.util.playLeaveQueueSound
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import java.util.*

object QueueService {

    private var queueUuid: UUID? = null

    suspend fun enterQueue() {
        if (queueUuid != null) {
            throw EnterQueueException("command.queue.enter.error.already_in_queue")
        }

        val lobby = LobbyService.getLobbyCopy() ?: throw EnterQueueException("command.queue.enter.error.no_lobby")

        if (lobby.getParticipantAmount() < lobby.getLobbySettings().size) {
            throw EnterQueueException(
                "command.queue.enter.error.not_enough_players",
                lobby.getParticipantAmount(),
                lobby.getLobbySettings().size
            )
        }

        // Make sure server is not in match yet
        try {
            val isInMatch = MatchService.checkInMatch()
            if (isInMatch) {
                throw EnterQueueException("command.queue.enter.error.already_in_match")
            }
        } catch (_: ApiException) {
            throw EnterQueueException("command.queue.enter.error.api_exception")
        }

        // Enter queue
        val queueEntryCreate = QueueEntryCreateMapper.fromLobby(lobby)
        try {
            val queueEntryCreateResponse = QueueDataAccess.enterQueue(queueEntryCreate)
            this.queueUuid = queueEntryCreateResponse.queueEntryUuid
            QueueTimerService.startTimer(lobby, queueEntryCreateResponse.queueEntryUuid)
        } catch (_: ApiException) {
            throw EnterQueueException("command.queue.enter.error.api_exception")
        }
        lobby.sendMessage("command.queue.enter.success")
        playEnterQueueSound(lobby)
    }

    suspend fun leaveQueue() {
        val queueUuidCopy = queueUuid ?: throw LeaveQueueException("command.queue.leave.error.not_in_queue")

        queueUuid = null
        QueueTimerService.stopTimer(queueUuidCopy)

        try {
            QueueDataAccess.leaveQueue(queueUuidCopy)

            LobbyService.getLobbyCopy()?.let {
                it.sendMessage("command.queue.leave.success")
                playLeaveQueueSound(it)
            }
        } catch (_: ApiException) {
            throw LeaveQueueException("command.queue.leave.error.api_exception")
        }
    }

    suspend fun parseQueueEntryStatus(queueEntryStatus: QueueEntryStatus) {
        if (queueEntryStatus.status == QueueEntryStatusType.MATCH_FOUND) {
            val queueUuidCopy = this.queueUuid!!
            QueueTimerService.stopTimer(queueUuidCopy)?.matchFound(matchUuid = queueEntryStatus.matchUuid!!)
            queueUuid = null
        }
        else if (queueEntryStatus.status == QueueEntryStatusType.LEFT_QUEUE && this.queueUuid != null) {
            LobbyService.getLobbyCopy()?.sendMessage("command.queue.leave.error.forced_to_leave_queue")
            try {
                leaveQueue()
            } catch (_: LeaveQueueException) { }
        }
    }
}