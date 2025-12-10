package net.serverwars.sunsetPlugin.domain.gameserver.services.mappers

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypes
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypesDto

object GameServerTypeMapper {

    fun fromDto(gameServerTypesDto: GameServerTypesDto): GameServerTypes {
        return GameServerTypes(
            types = gameServerTypesDto.types.map { GameServerType.fromValue(it)!! }
        )
    }

}