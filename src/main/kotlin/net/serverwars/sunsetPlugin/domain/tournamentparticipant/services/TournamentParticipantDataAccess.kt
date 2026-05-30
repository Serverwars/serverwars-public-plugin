package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services

import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import net.serverwars.sunsetPlugin.domain.queue.services.QueueDataAccess
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateResponseMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReady
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers.TournamentParticipantReadyMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.parse
import net.serverwars.sunsetPlugin.util.runAsync

object TournamentParticipantDataAccess {

    suspend fun readyServer(tournamentParticipantReady: TournamentParticipantReady): QueueEntryCreateResponse {
        val url = "${Config.getApiBaseUrl()}/tournamentParticipants/ready"
        val response = HttpClient.instance.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TournamentParticipantReadyMapper.toDto(tournamentParticipantReady))
        }

        val dto = response.parse<QueueEntryCreateResponseDto>("Could not ready for tournament")
        return QueueEntryCreateResponseMapper.fromDto(dto).also { result ->
            runAsync { QueueDataAccess.listenToQueueEvents(result.queueEntryUuid) } // Launch in background
        }
    }
}