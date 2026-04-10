package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

data class LobbySetAccessTypeOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val accessType: LobbyAccessType
): LobbyOperation {

    override fun execute(): Boolean {
        try {
            val lobby = LobbyService.updateLobbyAccessType(value = this.accessType)
            this.audience.sendTranslatedMessage("command.lobby.set.access_type.success", this.accessType.value)
            lobby.sendMessage("command.lobby.set.access_type.success.notify_lobby", this.accessType.value)
            if (this.accessType == LobbyAccessType.OPEN) {
                Audience.audience(Bukkit.getOnlinePlayers()).sendTranslatedMessage("command.lobby.set.access_type.success.open_announcement")
            }
            return true
        } catch (error: UpdateLobbyException) {
            this.audience.sendTranslatedMessage(error.key, this.accessType)
            return false
        }
    }

}
