package net.serverwars.sunsetPlugin.domain.gameserver.models

enum class GameServerType(val value: String) {
    RACING("racing"),
    WALLS("walls"),
    ;

    companion object {
        fun fromValue(value: String?): GameServerType? {
            return GameServerType.entries.find { it.value == value }
        }
    }
}
