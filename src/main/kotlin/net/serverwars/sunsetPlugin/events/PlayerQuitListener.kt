package net.serverwars.sunsetPlugin.events

import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        try {
            val lobby = LobbyService.playerLeaveLobby(event.player.uniqueId)
            lobby.sendMessage("command.lobby.leave.success.notify_lobby", event.player.name, lobby.participantUuids.size, lobby.lobbySettings.size)
        } catch (_: Exception) {}
    }
}