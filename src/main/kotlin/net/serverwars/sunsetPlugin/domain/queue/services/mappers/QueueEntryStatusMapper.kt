package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatus
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusDto
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrystatus.QueueEntryStatusType
import java.util.*

object QueueEntryStatusMapper {

    // From backend to MC server
    fun fromDto(queueEntryStatusDto: QueueEntryStatusDto): QueueEntryStatus {
        return QueueEntryStatus(
            matchUuid = queueEntryStatusDto.matchUuid?.let { UUID.fromString(it) },
            status = QueueEntryStatusType.fromValue(queueEntryStatusDto.status)!!,
            timeInQueue = queueEntryStatusDto.timeInQueue,
            errorMessage = queueEntryStatusDto.errorMessage,
        )
    }
}