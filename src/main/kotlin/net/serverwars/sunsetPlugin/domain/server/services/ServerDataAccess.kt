package net.serverwars.sunsetPlugin.domain.server.services

import io.ktor.client.request.*
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.server.models.Server
import net.serverwars.sunsetPlugin.domain.server.models.ServerDto
import net.serverwars.sunsetPlugin.domain.server.services.mappers.ServerMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.parse

object ServerDataAccess {

    suspend fun getServerFromSecret(serverSecret: String): Server {
        val url = "${Config.getApiBaseUrl()}/servers/secret/$serverSecret"
        val response = HttpClient.instance.get(url)
        val dto = response.parse<ServerDto>("Could not get server")
        return ServerMapper.fromDto(dto)
    }
}