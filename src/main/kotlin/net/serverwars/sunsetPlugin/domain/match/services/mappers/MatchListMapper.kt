package net.serverwars.sunsetPlugin.domain.match.services.mappers

import net.serverwars.sunsetPlugin.domain.match.models.matchlist.MatchList
import net.serverwars.sunsetPlugin.domain.match.models.matchlist.MatchListDto
import net.serverwars.sunsetPlugin.domain.match.models.matchlist.MatchSortOption

object MatchListMapper {

    fun fromDto(matchListDto: MatchListDto): MatchList {
        return MatchList(
            matches = matchListDto.matches.map { MatchMapper.fromDto(it) },
            sort = MatchSortOption.fromValue(matchListDto.sort) ?: MatchSortOption.RANDOM,
            pagination = matchListDto.pagination,
        )
    }
}