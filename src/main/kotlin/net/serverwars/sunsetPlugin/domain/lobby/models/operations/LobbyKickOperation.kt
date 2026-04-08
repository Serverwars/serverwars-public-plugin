package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

data class LobbyKickOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val kicked: Player
): LobbyOperation {

    override fun execute(): Boolean {
        try {
            val lobby = LobbyService.kickParticipantFromLobby(Participant(this.kicked.uniqueId, this.kicked.name))
            this.audience.sendTranslatedMessage("command.lobby.kick.success", this.kicked.name)
            lobby.sendMessage("command.lobby.kick.success.notify_lobby", this.kicked.name, lobby.getParticipantAmount(), Lobby.MAX_LOBBY_SIZE)
            this.kicked.sendTranslatedMessage("command.lobby.kick.success.notify_kickee")
            return true
        } catch (error: UpdateLobbyException) {
            this.audience.sendTranslatedMessage(error.key, this.kicked.name)
            return false
        }
    }

}
