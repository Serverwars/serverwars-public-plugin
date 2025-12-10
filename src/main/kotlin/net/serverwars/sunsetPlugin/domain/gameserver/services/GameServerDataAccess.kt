package net.serverwars.sunsetPlugin.domain.gameserver.services

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypes
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypesDto
import net.serverwars.sunsetPlugin.domain.gameserver.services.mappers.GameServerTypeMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException

object GameServerDataAccess {

    suspend fun getAvailableGameServerTypes(): GameServerTypes {
        val url = "${Config.getApiBaseUrl()}/gameServers/availableTypes"
        return runCatching {
            val response = HttpClient.instance.get(url)

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            val gameServerTypesDto = response.body<GameServerTypesDto>()
            GameServerTypeMapper.fromDto(gameServerTypesDto)
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not get available game server types: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
    }

}