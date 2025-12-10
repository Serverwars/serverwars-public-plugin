package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreate
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreateDto
import org.bukkit.Bukkit

object QueueEntryCreateMapper {

    // From lobby service
    fun fromLobby(lobby: Lobby): QueueEntryCreate {
        return QueueEntryCreate(
            serverSlug = lobby.serverSlug,
            playerUuids = lobby.participantUuids,
            gameType = lobby.lobbySettings.gameType,
            transferIP = if (Bukkit.getServer().isAcceptingTransfers && Bukkit.getIp() != "") "${Bukkit.getIp()}:${Bukkit.getPort()}" else null,
        )
    }

    // Send to backend
    fun toDto(queueEntryCreate: QueueEntryCreate): QueueEntryCreateDto {
        return QueueEntryCreateDto(
            serverSlug = queueEntryCreate.serverSlug,
            playerUuids = queueEntryCreate.playerUuids.map { it.toString() },
            gameType = queueEntryCreate.gameType.value,
            transferIP = queueEntryCreate.transferIP,
        )
    }
}