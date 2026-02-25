package net.serverwars.sunsetPlugin.domain.server.services

import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.server.exceptions.GetServerException
import net.serverwars.sunsetPlugin.domain.server.models.Server
import net.serverwars.sunsetPlugin.util.runAsync
import java.util.UUID

object ServerService {

    private var server: Server? = null

    fun reloadServer() {
        runAsync {
            val serverSecret = Config.getServerSecret()
            this.server = ServerDataAccess.getServerFromSecret(serverSecret)
        }
    }

    fun getServerUuid(): UUID = server?.serverUuid
        ?: throw GetServerException("Cached server is null. Is the secret correctly configured?")

    fun getServerSlug(): String = server?.slug
        ?: throw GetServerException("Cached server is null. Is the secret correctly configured?")

}