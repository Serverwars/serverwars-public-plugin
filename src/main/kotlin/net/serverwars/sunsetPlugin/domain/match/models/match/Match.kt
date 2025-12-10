package net.serverwars.sunsetPlugin.domain.match.models.match

import kotlinx.serialization.json.JsonElement
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatus
import java.util.*

data class Match(
    val matchUuid: UUID,
    val gameServerUuid: UUID?,
    val gameType: String,
    val createdAt: Long,
    val status: MatchStatus,
    val result: JsonElement?,
)
