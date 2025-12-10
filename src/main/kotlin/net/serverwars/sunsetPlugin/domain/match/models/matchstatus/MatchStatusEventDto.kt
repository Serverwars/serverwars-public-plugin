package net.serverwars.sunsetPlugin.domain.match.models.matchstatus

import kotlinx.serialization.Serializable

@Serializable
data class MatchStatusEventDto(
    val matchUuid: String,
    val status: String,
)
