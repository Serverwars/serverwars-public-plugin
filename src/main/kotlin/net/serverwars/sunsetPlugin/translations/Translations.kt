package net.serverwars.sunsetPlugin.translations

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.serverwars.sunsetPlugin.translations.TranslationManager.configuredLocale
import net.serverwars.sunsetPlugin.translations.TranslationManager.locales
import net.serverwars.sunsetPlugin.translations.exceptions.UnsupportedLocaleException
import java.text.MessageFormat

object Translations {

    fun get(key: String, vararg args: Any): Component {
        val properties = locales[configuredLocale.value]
            ?: throw UnsupportedLocaleException("Could not load translations for locale '${configuredLocale.value}'")
        val rawValue = properties.getProperty(key, key)

        val formatted = if (args.isNotEmpty()) {
            MessageFormat(rawValue).format(args)
        } else rawValue

        val prefix = when {
            "error." in key -> properties.getProperty("prefix.error_message", "prefix.error_message")
            "success." in key -> properties.getProperty("prefix.success_message", "prefix.success_message")
            else -> ""
        }

        return MiniMessage.miniMessage().deserialize("$prefix$formatted")
    }
}