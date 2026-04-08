package net.serverwars.sunsetPlugin.domain.gameservertype.services

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeList
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeListDto
import net.serverwars.sunsetPlugin.domain.gameservertype.services.mappers.GameServerTypeListMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException

object GameServerTypeDataAccess {

    suspend fun getAvailableGameServerTypes(): GameServerTypeList {
        val url = "${Config.getApiBaseUrl()}/gameServerTypes"
        return runCatching {
            val response = HttpClient.instance.get(url)

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            val gameServerTypeListDto = response.body<GameServerTypeListDto>()
            GameServerTypeListMapper.fromDto(gameServerTypeListDto)
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not get available game server types: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
    }

}