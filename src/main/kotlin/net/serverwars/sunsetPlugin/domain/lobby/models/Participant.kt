package net.serverwars.sunsetPlugin.domain.lobby.models

import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateParticipantException
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.ParticipantException
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

data class Participant(
    val playerUuid: UUID,
    val name: String
) {
    companion object {
        fun create(playerUuid: UUID): Participant {
            val player = Bukkit.getPlayer(playerUuid)
            if (player != null) {
                return Participant(
                    playerUuid = playerUuid,
                    name = player.name
                )
            } else {
                throw CreateParticipantException("Player $playerUuid not found.")
            }
        }
    }

    fun getAsPlayer(): Player = Bukkit.getOnlinePlayers().find { it.uniqueId == this.playerUuid }
        ?: throw ParticipantException("Could not convert participant with uuid \"${this.playerUuid}\" to an online player.")
}
