package net.serverwars.sunsetPlugin.domain.lobby.models

import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateParticipantException
import org.bukkit.Bukkit
import java.util.UUID

data class Participant(
    val playerUuid: UUID,
    val name: String
) {
    companion object {
        fun create(playerUuid: UUID): Participant {
            val player = Bukkit.getOfflinePlayer(playerUuid)
            if (player.hasPlayedBefore()) {
                return Participant(
                    playerUuid = playerUuid,
                    name = player.name!!
                )
            } else {
                throw CreateParticipantException("Player $playerUuid not found.")
            }
        }
    }
}
