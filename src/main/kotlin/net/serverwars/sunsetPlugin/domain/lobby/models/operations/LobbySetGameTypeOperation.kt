package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

data class LobbySetGameTypeOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val gameType: GameServerType
): LobbyOperation {

    override fun execute(): Boolean {
        try {
            val lobby = LobbyService.updateLobbyGameType(value = this.gameType)
            this.audience.sendTranslatedMessage("command.lobby.set.game_type.success", this.gameType.name)
            lobby.sendMessage("command.lobby.set.game_type.success.notify_lobby", this.gameType.name)
            return true
        } catch (error: UpdateLobbyException) {
            this.audience.sendTranslatedMessage(error.key, this.gameType.name)
            return false
        }
    }

}
