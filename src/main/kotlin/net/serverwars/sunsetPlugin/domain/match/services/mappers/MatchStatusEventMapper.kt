package net.serverwars.sunsetPlugin.domain.match.services.mappers

import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatus
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatusEvent
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatusEventDto
import java.util.*

object MatchStatusEventMapper {

    // From backend to MC server
    fun fromDto(matchStatusEventDto: MatchStatusEventDto): MatchStatusEvent {
        return MatchStatusEvent(
            matchUuid = UUID.fromString(matchStatusEventDto.matchUuid),
            status = MatchStatus.fromValue(matchStatusEventDto.status)!!,
        )
    }
}