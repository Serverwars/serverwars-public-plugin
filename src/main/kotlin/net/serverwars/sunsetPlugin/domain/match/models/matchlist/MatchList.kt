package net.serverwars.sunsetPlugin.domain.match.models.matchlist

import net.serverwars.sunsetPlugin.domain.match.models.match.Match
import net.serverwars.sunsetPlugin.util.pagination.PaginationResult

data class MatchList(
    val matches: List<Match>,
    val sort: MatchSortOption,
    val pagination: PaginationResult,
)
