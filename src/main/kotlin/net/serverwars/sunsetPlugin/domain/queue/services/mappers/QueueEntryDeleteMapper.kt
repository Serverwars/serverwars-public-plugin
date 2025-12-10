package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.domain.queue.models.queueentrydelete.QueueEntryDeleteDto
import java.util.*

object QueueEntryDeleteMapper {

    // From backend to MC server
    fun toDto(queueEntryUuid: UUID): QueueEntryDeleteDto {
        return QueueEntryDeleteDto(queueEntryUuid = queueEntryUuid.toString())
    }
}