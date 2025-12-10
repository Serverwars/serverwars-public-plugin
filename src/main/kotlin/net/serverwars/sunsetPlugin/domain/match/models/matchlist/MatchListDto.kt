package net.serverwars.sunsetPlugin.domain.match.models.matchlist

import kotlinx.serialization.Serializable
import net.serverwars.sunsetPlugin.domain.match.models.match.MatchDto
import net.serverwars.sunsetPlugin.util.pagination.PaginationResult

@Serializable
data class MatchListDto(
    val matches: List<MatchDto>,
    val sort: String,
    val pagination: PaginationResult,
)
