package net.serverwars.sunsetPlugin.config

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.exceptions.ConfigInvalidValueException
import net.serverwars.sunsetPlugin.config.exceptions.ConfigKeyNotFoundException
import net.serverwars.sunsetPlugin.translations.SupportedLocale
import java.util.*

object Config {

    private val props: Properties by lazy {
        val stream = this::class.java.getResourceAsStream("/plugin.properties")
            ?: error("plugin.properties not found in resources")
        Properties().apply {
            load(stream)
        }
    }

    fun getApiBaseUrl(): String {
        val backendUrl = props["backend.url"] ?: error("backend.url not set in properties")
        return "$backendUrl/sunset/v1"
    }

    fun getServerwarsMinecraftServerIP(): String {
        val ip = props["serverwars.minecraft.server.ip"] ?: error("minecraft.server.ip not set in properties")
        return ip.toString()
    }

    fun getLocale(): SupportedLocale {
        val value = Main.inst.config.getString("locale")
            ?: throw ConfigKeyNotFoundException("Could not find key 'locale' from plugin config.")
        return SupportedLocale.fromValue(value)
            ?: throw ConfigInvalidValueException("Key 'locale' has an invalid value in the plugin config.")
    }

    fun getServerSecret(): String {
        return Main.inst.config.getString("server_secret")
            ?: throw ConfigKeyNotFoundException("Could not find key 'server_secret' in plugin config.")
    }

    fun shouldTransferOnMatchReady(): Boolean {
        return Main.inst.config.getBoolean("transfer_on_match_ready", true)
    }
}