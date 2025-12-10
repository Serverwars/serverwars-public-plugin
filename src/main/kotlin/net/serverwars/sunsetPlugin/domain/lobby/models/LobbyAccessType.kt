package net.serverwars.sunsetPlugin.domain.lobby.models

enum class LobbyAccessType(val value: String) {
    OPEN("open"),
    INVITE_ONLY("invite-only");

    companion object {
        fun fromValue(value: String?): LobbyAccessType? {
            return entries.find { it.value == value }
        }
    }
}