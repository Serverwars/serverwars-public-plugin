package net.serverwars.sunsetPlugin.domain.match.models.match

import kotlinx.serialization.json.JsonElement
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatus
import java.util.*

data class Match(
    val matchUuid: UUID,
    val gameServerUuid: UUID?,
    val gameType: GameServerType,
    val createdAt: Long,
    val status: MatchStatus,
    val result: JsonElement?,
)
