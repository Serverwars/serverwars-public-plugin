package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

data class LobbyJoinOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val joiner: Player
): LobbyOperation {

    override fun execute(): Boolean {
        try {
            val lobby = LobbyService.playerJoinLobby(this.joiner.uniqueId)
            this.joiner.sendTranslatedMessage("command.lobby.join.success")
            lobby.sendMessage("command.lobby.join.success.notify_lobby", this.joiner.name, lobby.getParticipantAmount(), Lobby.MAX_LOBBY_SIZE)
            return true
        } catch (error: UpdateLobbyException) {
            this.joiner.sendTranslatedMessage(error.key, this.joiner.name)
            return false
        }
    }

}
