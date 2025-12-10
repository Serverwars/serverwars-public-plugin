package net.serverwars.sunsetPlugin.domain.match.models.match

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MatchDto(
    val matchUuid: String,
    val gameServerUuid: String?,
    val gameType: String,
    val createdAt: Long,
    val status: String,
    val result: JsonElement?,
)
