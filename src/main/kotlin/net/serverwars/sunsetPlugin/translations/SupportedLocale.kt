package net.serverwars.sunsetPlugin.translations

enum class SupportedLocale(val value: String) {
    EN_US("en_US"),
    NL_BE("nl_BE"),
    ;

    companion object {
        fun fromValue(value: String?): SupportedLocale? {
            return SupportedLocale.entries.find { it.value == value }
        }
    }
}