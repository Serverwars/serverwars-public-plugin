package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer.QueueEntryPlayer
import net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer.QueueEntryPlayerDto

object QueueEntryPlayerMapper {

    fun fromLobby(participant: Participant): QueueEntryPlayer {
        return QueueEntryPlayer(
            playerUuid = participant.playerUuid,
            name = participant.name,
        )
    }

    // From backend to MC server
    fun toDto(queueEntryPlayer: QueueEntryPlayer): QueueEntryPlayerDto {
        return QueueEntryPlayerDto(
            playerUuid = queueEntryPlayer.playerUuid.toString(),
            name = queueEntryPlayer.name,
        )
    }
}