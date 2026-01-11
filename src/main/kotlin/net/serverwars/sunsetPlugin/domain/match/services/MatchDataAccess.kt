package net.serverwars.sunsetPlugin.domain.match.services

import io.ktor.client.call.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.timeout
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.models.matchlist.MatchList
import net.serverwars.sunsetPlugin.domain.match.models.matchlist.MatchListDto
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatus
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatusEventDto
import net.serverwars.sunsetPlugin.domain.match.services.mappers.MatchListMapper
import net.serverwars.sunsetPlugin.domain.match.services.mappers.MatchStatusEventMapper
import net.serverwars.sunsetPlugin.domain.server.services.ServerService
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import net.serverwars.sunsetPlugin.util.rest.parseSSEDto
import java.util.*
import kotlin.time.Duration.Companion.minutes

object MatchDataAccess {

    suspend fun getActiveMatchList(): MatchList {
        val url = "${Config.getApiBaseUrl()}/matches"
        return runCatching {
            val response = HttpClient.instance.get(url) {
                parameters {
                    parameter("filter_server_slug", ServerService.getServerSlug())
                    parameter("filter_in_session", true)
                }
            }

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            val matchListDto = response.body<MatchListDto>()
            MatchListMapper.fromDto(matchListDto)
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not get active matches: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun listenToMatchStatusEvents(matchUuid: UUID) {
        val url = "${Config.getApiBaseUrl()}/matches/$matchUuid/status"

        runCatching {
            coroutineScope {
                HttpClient.instance.sse(url) {
                    incoming
                        .timeout(2.minutes)
                        .catch { e ->
                            if (e is TimeoutCancellationException) error("Timeout - no events for 2 minutes")
                            else throw e
                        }
                        .collect { event ->
                            val rawData = event.data ?: return@collect
                            if (rawData != "Keep alive") {
                                val dto = parseSSEDto<MatchStatusEventDto>(rawData)
                                val matchStatusEvent = MatchStatusEventMapper.fromDto(dto)

                                Main.inst.logger.info("[MATCH_STATUS_EVENT]: $matchStatusEvent")
                                if (matchStatusEvent.status === MatchStatus.OPEN) {
                                    LobbyService.sendLobbyToMatch()
                                    this@coroutineScope.cancel() // Close SSE connection
                                }
                            }
                        }
                }
            }
        }.onFailure { error ->
            if (error !is CancellationException) {
                Main.inst.logger.severe("[MATCH STATUS LISTEN Error]: $error")
            }
        }
    }

}