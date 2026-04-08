package net.serverwars.sunsetPlugin.domain.lobby.models

import org.bukkit.Material

enum class LobbyAccessType(
    val value: String,
    val material: Material,
    val title: String,
    val description: String,
) {
    OPEN(
        value = "open",
        material = Material.OAK_DOOR,
        title = "Open",
        description = "Any player on this server can join the Serverwars lobby.",
    ),
    INVITE_ONLY(
        value = "invite-only",
        material = Material.IRON_DOOR,
        title = "Invite only",
        description = "Only invited players can join the Serverwars lobby.",
    ),
    ;

    companion object {
        fun fromValue(value: String?): LobbyAccessType? {
            return entries.find { it.value == value }
        }
    }
}