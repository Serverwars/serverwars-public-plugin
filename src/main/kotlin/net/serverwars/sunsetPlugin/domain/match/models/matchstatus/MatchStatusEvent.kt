package net.serverwars.sunsetPlugin.domain.match.models.matchstatus

import java.util.UUID

data class MatchStatusEvent(
    val matchUuid: UUID,
    val status: MatchStatus,
)
