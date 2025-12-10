package net.serverwars.sunsetPlugin

import net.serverwars.sunsetPlugin.events.manager.EventListenerRegistrar
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
        EventListenerRegistrar.registerEventListeners()

        // Translations
        TranslationManager.loadTranslations()
    }

}
