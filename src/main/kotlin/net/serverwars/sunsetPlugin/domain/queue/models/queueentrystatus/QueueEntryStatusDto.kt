package net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus

import kotlinx.serialization.Serializable

@Serializable
data class QueueEntryStatusDto(
    val matchUuid: String? = null,
    val timeInQueue: Long? = null,
    val errorMessage: String? = null,
    val status: String,
)