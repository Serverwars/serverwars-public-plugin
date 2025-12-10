package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponse
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreateresponse.QueueEntryCreateResponseDto
import java.util.*

object QueueEntryCreateResponseMapper {

    // From backend to MC server
    fun fromDto(queueEntryCreateResponseDto: QueueEntryCreateResponseDto): QueueEntryCreateResponse {
        return QueueEntryCreateResponse(
            queueEntryUuid = UUID.fromString(queueEntryCreateResponseDto.queueEntryUuid)
        )
    }
}