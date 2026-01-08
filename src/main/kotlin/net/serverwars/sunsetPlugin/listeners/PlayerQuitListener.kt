package net.serverwars.sunsetPlugin.listeners

import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object PlayerQuitListener : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        try {
            val lobby = LobbyService.participantLeaveLobby(Participant(event.player.uniqueId, event.player.name))
            lobby.sendMessage("command.lobby.leave.success.notify_lobby", event.player.name, lobby.getParticipantAmount(), lobby.getLobbySettings().size)
        } catch (_: Exception) {}
    }
}