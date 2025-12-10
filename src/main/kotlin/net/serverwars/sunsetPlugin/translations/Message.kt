package net.serverwars.sunsetPlugin.translations

import net.kyori.adventure.audience.Audience

fun Audience.sendTranslatedMessage(key: String, vararg args: Any) {
    sendMessage(Translations.get(key, *args))
}

fun Audience.sendTranslatedActionBarMessage(key: String, vararg args: Any) {
    sendActionBar(Translations.get(key, *args))
}