package net.serverwars.sunsetPlugin.domain.tournamentparticipant.services

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import net.serverwars.sunsetPlugin.domain.queue.services.QueueDataAccess
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateResponseMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.exceptions.TournamentParticipantReadyException
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantenter.TournamentParticipantEnter
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.models.tournamentparticipantready.TournamentParticipantReady
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers.TournamentParticipantEnterMapper
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.mappers.TournamentParticipantReadyMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
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

        if (response.status == HttpStatusCode.Conflict) {
            throw TournamentParticipantReadyException("command.tournament.ready.error.already_ready")
        }
        if (response.status == HttpStatusCode.NotFound) {
            throw TournamentParticipantReadyException("command.tournament.ready.error.no_tournament")
        }

        if (!response.status.isSuccess()) {
            Main.inst.logger.severe("[API EXCEPTION] Could not tournament ready: ${response.status} Response body: ${response.body() ?: ""}")
            throw ApiException()
        }
    }

    suspend fun enterServer(tournamentParticipantEnter: TournamentParticipantEnter): QueueEntryCreateResponse =
        runCatching {
            val url = "${Config.getApiBaseUrl()}/tournamentParticipants/enter"
            val response = HttpClient.instance.post(url) {
                contentType(ContentType.Application.Json)
                setBody(TournamentParticipantEnterMapper.toDto(tournamentParticipantEnter))
            }

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            QueueEntryCreateResponseMapper.fromDto(response.body<QueueEntryCreateResponseDto>())
                .also { result -> runAsync { QueueDataAccess.listenToQueueEvents(result.queueEntryUuid) } } // Launch in background
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not tournament enter: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
}