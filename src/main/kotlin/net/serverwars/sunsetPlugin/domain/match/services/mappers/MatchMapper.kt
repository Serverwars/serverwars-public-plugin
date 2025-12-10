package net.serverwars.sunsetPlugin.domain.match.services.mappers

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import net.serverwars.sunsetPlugin.domain.match.models.match.Match
import net.serverwars.sunsetPlugin.domain.match.models.match.MatchDto
import net.serverwars.sunsetPlugin.domain.match.models.matchstatus.MatchStatus
import java.util.*

object MatchMapper {

    fun fromDto(matchDto: MatchDto): Match {
        return Match(
            matchUuid = UUID.fromString(matchDto.matchUuid),
            gameServerUuid = matchDto.gameServerUuid?.let { UUID.fromString(it) },
            createdAt = matchDto.createdAt,
            status = MatchStatus.fromValue(matchDto.status) ?: MatchStatus.ERROR,
            gameType = GameServerType.fromValue(matchDto.gameType)!!,
            result = matchDto.result,
        )
    }
}