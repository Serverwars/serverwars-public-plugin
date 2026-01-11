package net.serverwars.sunsetPlugin.domain.server.services.mappers

import net.serverwars.sunsetPlugin.domain.server.models.Server
import net.serverwars.sunsetPlugin.domain.server.models.ServerDto
import java.util.UUID

object ServerMapper {

    fun fromDto(serverDto: ServerDto): Server {
        return Server(
            serverUuid = UUID.fromString(serverDto.serverUuid),
            slug = serverDto.slug,
        )
    }

}