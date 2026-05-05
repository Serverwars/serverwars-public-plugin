package net.serverwars.sunsetPlugin.domain.gameservertype.services

import io.ktor.client.request.*
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeList
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist.GameServerTypeListDto
import net.serverwars.sunsetPlugin.domain.gameservertype.services.mappers.GameServerTypeListMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.parse

object GameServerTypeDataAccess {

    suspend fun getAvailableGameServerTypes(): GameServerTypeList {
        val url = "${Config.getApiBaseUrl()}/gameServerTypes"
        val response = HttpClient.instance.get(url)
        val dto = response.parse<GameServerTypeListDto>("Could not get available game server types")
        return GameServerTypeListMapper.fromDto(dto)
    }

}