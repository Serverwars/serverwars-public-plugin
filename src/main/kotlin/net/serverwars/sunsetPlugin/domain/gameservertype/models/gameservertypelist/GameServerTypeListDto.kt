package net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist

import kotlinx.serialization.Serializable
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerTypeDto
import net.serverwars.sunsetPlugin.util.pagination.PaginationResult

@Serializable
data class GameServerTypeListDto(
    val gameServerTypes: List<GameServerTypeDto>,
    val sort: String,
    val pagination: PaginationResult,
)
