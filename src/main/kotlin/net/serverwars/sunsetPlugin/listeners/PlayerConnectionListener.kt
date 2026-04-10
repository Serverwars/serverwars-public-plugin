package net.serverwars.sunsetPlugin.listeners

import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.runAsync
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PlayerConnectionListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        runAsync {
            if (MatchService.checkInMatch()) {
                event.player.sendTranslatedMessage("match_active")
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        try {
            val lobby = LobbyService.participantLeaveLobby(Participant(event.player.uniqueId, event.player.name))
            lobby.sendMessage("command.lobby.leave.success.notify_lobby", event.player.name, lobby.getParticipantAmount(), Lobby.MAX_LOBBY_SIZE)
        } catch (_: Exception) {}
    }
}