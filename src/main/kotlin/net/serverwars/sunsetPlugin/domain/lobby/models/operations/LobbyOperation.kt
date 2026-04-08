package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import org.bukkit.entity.Player

interface LobbyOperation {
    val audience: Audience
    val executor: Player?

    fun execute(): Boolean
}
