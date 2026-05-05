package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services

import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import net.serverwars.sunsetPlugin.domain.queue.services.QueueDataAccess
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateResponseMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter.TournamentParticipantEnter
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReady
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers.TournamentParticipantEnterMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers.TournamentParticipantReadyMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.parse
import net.serverwars.sunsetPlugin.util.runAsync

object TournamentParticipantDataAccess {

    suspend fun readyServer() {
        val url = "${Config.getApiBaseUrl()}/tournamentParticipants/ready"
        val ready = TournamentParticipantReady(
            serverSecret = Config.getServerSecret(),
        )

        val response = HttpClient.instance.put(url) {
            contentType(ContentType.Application.Json)
            setBody(TournamentParticipantReadyMapper.toDto(ready))
        }

        response.parse<Any>("Could not enter tournament")
    }

    suspend fun enterServer(tournamentParticipantEnter: TournamentParticipantEnter): QueueEntryCreateResponse {
        val url = "${Config.getApiBaseUrl()}/tournamentParticipants/enter"
        val response = HttpClient.instance.post(url) {
            contentType(ContentType.Application.Json)
            setBody(TournamentParticipantEnterMapper.toDto(tournamentParticipantEnter))
        }

        val dto = response.parse<QueueEntryCreateResponseDto>("Could not enter tournament")
        return QueueEntryCreateResponseMapper.fromDto(dto).also { result ->
            runAsync { QueueDataAccess.listenToQueueEvents(result.queueEntryUuid) } // Launch in background
        }
    }
}