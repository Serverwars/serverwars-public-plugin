package net.serverwars.sunsetPlugin.domain.queue.services

import io.ktor.client.plugins.sse.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.timeout
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.queue.exceptions.QueueLeaveException
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreate
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusDto
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusType
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryCreateResponseMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryDeleteMapper
import net.serverwars.sunsetPlugin.domain.queue.services.mappers.QueueEntryStatusMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.parse
import net.serverwars.sunsetPlugin.util.rest.parseSSEDto
import net.serverwars.sunsetPlugin.util.runAsync
import java.util.*
import kotlin.time.Duration.Companion.seconds

object QueueDataAccess {

    private var stopListening = false

    suspend fun enterQueue(queueEntryCreate: QueueEntryCreate): QueueEntryCreateResponse {
        val url = "${Config.getApiBaseUrl()}/queue/enter"
        val response = HttpClient.instance.post(url) {
            contentType(ContentType.Application.Json)
            setBody(QueueEntryCreateMapper.toDto(queueEntryCreate))
        }
        val dto = response.parse<QueueEntryCreateResponseDto>("Could not enter queue")
        return QueueEntryCreateResponseMapper.fromDto(dto).also { result ->
            runAsync { listenToQueueEvents(result.queueEntryUuid) } // Launch in background
        }
    }

    suspend fun leaveQueue(queueUuid: UUID) {
        this.stopListening = true

        val url = "${Config.getApiBaseUrl()}/queue/leave"
        val response = HttpClient.instance.put(url) {
            contentType(ContentType.Application.Json)
            setBody(QueueEntryDeleteMapper.toDto(queueUuid))
        }
        response.parse<Any>("Could not leave queue")
    }

    @OptIn(FlowPreview::class)
    suspend fun listenToQueueEvents(queueUuid: UUID) {
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

                        val statusMessage = when (queueStatus.status) {
                            QueueEntryStatusType.NO_MATCH_FOUND -> null // Still in queue
                            QueueEntryStatusType.LEFT_QUEUE -> null // Already logged
                            QueueEntryStatusType.TOURNAMENT_CANCELLED -> "Tournament cancelled"
                            QueueEntryStatusType.MATCH_FOUND -> "Match found after ${QueueService.getTimeInQueue()}ms! Preparing game server..."
                            QueueEntryStatusType.ERROR -> "Error: ${queueStatus.errorMessage}"
                        }
                        statusMessage?.let { Main.inst.logger.info("[QUEUE] $it") }
                        QueueService.parseQueueEntryStatus(queueStatus)
                    }
            }

        }.onFailure { error ->
            if (error is SSEClientException && stopListening) {
                stopListening = false
            } else {
                Main.inst.logger.severe("[QUEUE LISTEN Error] $error")
                try {
                    QueueService.leaveQueue()
                } catch (_: QueueLeaveException) {
                }
            }
        }
    }
}