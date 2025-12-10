package net.serverwars.sunsetPlugin

import net.serverwars.sunsetPlugin.domain.gameserver.services.GameServerService
import net.serverwars.sunsetPlugin.listeners.EventListenerManager
import net.serverwars.sunsetPlugin.translations.TranslationManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        lateinit var inst: Main
    }

    override fun onEnable() {
        inst = this

        // Save default config resource into the plugin’s data directory if it does not yet exist
        saveDefaultConfig()

        // Event listeners
        EventListenerManager.initialize()

        // Translations
        TranslationManager.loadTranslations()

        // Fetch available Game server types
        GameServerService.initialize()
    }

}
