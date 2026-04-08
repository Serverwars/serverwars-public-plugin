package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.DeleteLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

data class LobbyDeleteOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val confirmed: Boolean,
): LobbyOperation {

    override fun execute(): Boolean {
        return if (this.confirmed) confirmedDelete() else unconfirmedDelete()
    }

    private fun unconfirmedDelete(): Boolean {
        if (!LobbyService.lobbyExists()) {
            this.audience.sendTranslatedMessage("command.lobby.delete.error.no_lobby")
            return false
        }
        this.audience.sendTranslatedMessage("command.lobby.delete.error.warning")
        return true
    }

    private fun confirmedDelete(): Boolean {
        try {
            val lobby = LobbyService.deleteLobby()
            lobby.sendMessage("command.lobby.delete.success.notify_lobby")
            this.audience.sendTranslatedMessage("command.lobby.delete.success")
            return true
        } catch (error: DeleteLobbyException) {
            this.audience.sendTranslatedMessage(error.key, *error.args)
            return false
        }
    }
}
