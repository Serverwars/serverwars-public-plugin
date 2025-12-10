package net.serverwars.sunsetPlugin.domain.queue.models.queueentrydelete

import kotlinx.serialization.Serializable

@Serializable
data class QueueEntryDeleteDto(
    val queueEntryUuid: String,
)