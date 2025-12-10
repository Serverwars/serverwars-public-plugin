package net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus

import java.util.*

data class QueueEntryStatus(
    val matchUuid: UUID? = null,
    val timeInQueue: Long? = null,
    val errorMessage: String? = null,
    val status: QueueEntryStatusType,
)