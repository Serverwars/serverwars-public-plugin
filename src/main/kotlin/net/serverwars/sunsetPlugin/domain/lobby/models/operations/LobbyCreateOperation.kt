package net.serverwars.sunsetPlugin.domain.lobby.models.operations

import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

data class LobbyCreateOperation(
    override val audience: Audience,
    override val executor: Player?,
    private val lobbySettings: LobbySettings,
): LobbyOperation {

    override fun execute(): Boolean {
        try {
            LobbyService.createLobby(
                accessType = this.lobbySettings.accessType,
                gameType = this.lobbySettings.gameType
            )
            this.audience.sendTranslatedMessage("command.lobby.create.success")

            if (this.lobbySettings.accessType == LobbyAccessType.OPEN) {
                if (this.executor != null) {
                    Bukkit.getServer().sendTranslatedMessage("command.lobby.create.success.open_announcement_by_player", executor.name)
                } else {
                    Audience.audience(Bukkit.getOnlinePlayers()).sendTranslatedMessage("command.lobby.create.success.open_announcement")
                }
            }

            if (this.executor != null) {
                LobbyService.playerJoinLobby(this.executor.uniqueId, true)
            }

            return true
        } catch (error: CreateLobbyException) {
            this.audience.sendTranslatedMessage(error.key, *error.args)
            return false
        }
    }
}
