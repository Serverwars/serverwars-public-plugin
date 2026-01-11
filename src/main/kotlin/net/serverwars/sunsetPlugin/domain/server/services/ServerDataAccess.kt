package net.serverwars.sunsetPlugin.domain.server.services

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.server.models.Server
import net.serverwars.sunsetPlugin.domain.server.models.ServerDto
import net.serverwars.sunsetPlugin.domain.server.services.mappers.ServerMapper
import net.serverwars.sunsetPlugin.util.rest.HttpClient
import net.serverwars.sunsetPlugin.util.rest.exceptions.ApiException

object ServerDataAccess {

    suspend fun getServerFromSecret(serverSecret: String): Server {
        val url = "${Config.getApiBaseUrl()}/servers/secret/$serverSecret"
        return runCatching {
            val response = HttpClient.instance.get(url)

            if (!response.status.isSuccess()) {
                error("${response.status} Response body: ${response.body() ?: ""}")
            }

            val serverDto = response.body<ServerDto>()
            ServerMapper.fromDto(serverDto)
        }.getOrElse { error ->
            Main.inst.logger.severe("[API EXCEPTION] Could not get server: ${error.message ?: "Unknown error"}")
            throw ApiException()
        }
    }
}