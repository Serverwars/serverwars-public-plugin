package net.serverwars.sunsetPlugin.domain.gameservertype.services.mappers

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerTypeDto
import org.bukkit.Material

object GameServerTypeMapper {

    fun fromDto(gameServerTypeDto: GameServerTypeDto): GameServerType {
        return GameServerType(
            name = gameServerTypeDto.name,
            material = Material.valueOf(gameServerTypeDto.material),
            description = gameServerTypeDto.description
        )
    }

}