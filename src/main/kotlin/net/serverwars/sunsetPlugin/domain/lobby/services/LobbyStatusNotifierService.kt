package net.serverwars.sunsetPlugin.domain.lobby.services

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import org.bukkit.Bukkit

object LobbyStatusNotifierService {

    private var showLobbyStatusTaskId: Int? = null

    fun startShowingLobbyStatus() {
        stopShowingLobbyStatus()
        this.showLobbyStatusTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.inst, {
            showLobbyStatus()
        }, 0L, 40L)
    }

    fun stopShowingLobbyStatus() {
        val taskId = this.showLobbyStatusTaskId
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId)
            this.showLobbyStatusTaskId = null
        }
    }

    private fun showLobbyStatus() {
        val lobby = LobbyService.getLobbyCopy() ?: return

        lobby.sendActionBarMessage("lobby.status.action_bar", lobby.getLobbySettings().gameType.replaceFirstChar { it.uppercase() }, lobby.getParticipants().size, Lobby.MAX_LOBBY_SIZE)
    }


}