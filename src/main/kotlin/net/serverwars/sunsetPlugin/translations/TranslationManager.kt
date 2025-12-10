package net.serverwars.sunsetPlugin.translations

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.config.Config
import java.io.File
import java.util.*


object TranslationManager {

    val locales: MutableMap<String, Properties> = mutableMapOf()
    lateinit var configuredLocale: SupportedLocale

    /** Initialize translations from config and disk */
    fun loadTranslations() {
        // Read locale from config
        this.configuredLocale = Config.getLocale()

        // Reset loaded translations
        this.locales.clear()
        ensureLangFolderExists()

        // Load translations
        SupportedLocale.entries.forEach {
            val translationFilePath = "lang/${it.value}.properties"
            val translationFile = File(Main.inst.dataFolder, translationFilePath)
            val props = Properties()
            translationFile.inputStream().use { stream -> props.load(stream) }
            locales[it.value] = props
        }
    }

    private fun ensureLangFolderExists() {
        // Ensure lang folder exists
        val langFolder = File(Main.inst.dataFolder, "lang")
        if (!langFolder.exists()) langFolder.mkdirs()

        // Save default translation files if missing
        SupportedLocale.entries.forEach {
            saveResourceIfNotExists("lang/${it.value}.properties")
        }
    }

    private fun saveResourceIfNotExists(resourcePath: String) {
        val file = File(Main.inst.dataFolder, resourcePath)
        if (!file.exists()) {
            Main.inst.saveResource(resourcePath, false)
        }
    }
}