package net.serverwars.sunsetPlugin.domain.server.services

import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.server.exceptions.GetServerException
import net.serverwars.sunsetPlugin.domain.server.models.Server
import net.serverwars.sunsetPlugin.util.rest.exceptions.callApi
import java.util.UUID

object ServerService {

    private var server: Server? = null

    fun reloadServer() {
        callApi {
            val serverSecret = Config.getServerSecret()
            this.server = ServerDataAccess.getServerFromSecret(serverSecret)
        }
    }

    fun getServerUuid(): UUID = this.server?.serverUuid
        ?: throw GetServerException("Cached server is null. Is the secret correctly configured in the plugin's config?")

    fun getServerSlug(): String = this.server?.slug
        ?: throw GetServerException("Cached server is null. Is the secret correctly configured in the plugin's config?")

}