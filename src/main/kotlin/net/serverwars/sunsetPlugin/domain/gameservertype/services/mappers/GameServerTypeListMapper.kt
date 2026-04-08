package net.serverwars.sunsetPlugin.domain.gameservertype.services.mappers

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeList
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeListDto
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeSortOption

object GameServerTypeListMapper {

    fun fromDto(gameServerTypeListDto: GameServerTypeListDto): GameServerTypeList {
        return GameServerTypeList(
            gameServerTypes = gameServerTypeListDto.gameServerTypes.map {  GameServerTypeMapper.fromDto(it) },
            sort = GameServerTypeSortOption.fromValue(gameServerTypeListDto.sort) ?: GameServerTypeSortOption.RANDOM,
            pagination = gameServerTypeListDto.pagination,
        )
    }

}