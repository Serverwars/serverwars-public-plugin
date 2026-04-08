package net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertypelist

enum class GameServerTypeSortOption(val value: String) {
    RANDOM("random");

    companion object {
        fun fromValue(value: String?): GameServerTypeSortOption? {
            return entries.find { it.value == value }
        }
    }

}