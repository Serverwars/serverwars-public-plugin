package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse

import kotlinx.serialization.Serializable

@Serializable
data class QueueEntryCreateResponseDto(
    val queueEntryUuid: String,
)