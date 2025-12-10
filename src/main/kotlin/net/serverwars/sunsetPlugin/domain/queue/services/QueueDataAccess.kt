package net.serverwars.sunsetPlugin.domain.queue.services

import io.ktor.client.call.*
import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.timeout
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.queue.exceptions.LeaveQueueException
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreate
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusDto
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateResponseMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryDeleteMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryStatusMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException
import net.serverwars.sunsetPlugin.util.rest.parseSSEDto
import net.serverwars.sunsetPlugin.util.runAsync
import java.util.*
import kotlin.time.Duration.Companion.seconds

object QueueDataAccess {

    private var stopListening = false

    suspend fun enterQueue(queueEntryCreate: QueueEntryCreate): QueueEntryCreateResponse =
        runCatching {
            val url = "${Config.getApiBaseUrl()}/queue/enter"
            val response = HttpClient.instance.post(url) {
                contentType(ContentType.Application.Json)
                setBody(QueueEntryCreateMapper.toDto(queueEntryCreate))
            }

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            QueueEntryCreateResponseMapper.fromDto(response.body<QueueEntryCreateResponseDto>())
                .also { result -> runAsync { listenToQueueEvents(result.queueEntryUuid) } } // Launch in background
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not enter queue: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }

    suspend fun leaveQueue(queueUuid: UUID) {
        stopListening = true
        runCatching {
            val url = "${Config.getApiBaseUrl()}/queue/leave"
            val response = HttpClient.instance.put(url) {
                contentType(ContentType.Application.Json)
                setBody(QueueEntryDeleteMapper.toDto(queueUuid))
            }

            if (!response.status.isSuccess()) {
                val body = response.body<String>()
                error("${response.status} Response body: $body")
            }
        }.onFailure { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not leave queue: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun listenToQueueEvents(queueUuid: UUID) {
        val url = "${Config.getApiBaseUrl()}/queue/$queueUuid"

        runCatching {
            HttpClient.instance.sse(url) {
                incoming
                    .timeout(10.seconds)
                    .catch { e ->
                        if (e is TimeoutCancellationException) error("Timeout - no events for 10s")
                        else throw e
                    }
                    .collect { event ->
                        val rawData = event.data ?: return@collect
                        val dto = parseSSEDto<QueueEntryStatusDto>(rawData)
                        val queueStatus = QueueEntryStatusMapper.fromDto(dto)

                        Main.inst.logger.info("[QUEUE]: $queueStatus")
                        QueueService.parseQueueEntryStatus(queueStatus)
                    }
            }

        }.onFailure { error ->
            if (error is SSEClientException && stopListening) {
                stopListening = false
            } else {
                Main.inst.logger.severe("[QUEUE LISTEN Error]: $error")
                try {
                    QueueService.leaveQueue()
                } catch (_: LeaveQueueException) {
                }
            }
        }
    }
}