package net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.util.pagination.PaginationResult

data class GameServerTypeList(
    val gameServerTypes: List<GameServerType>,
    val sort: GameServerTypeSortOption,
    val pagination: PaginationResult
)
