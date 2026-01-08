package net.serverwars.sunsetPlugin.domain.queue.services.mappers

import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreate
import net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate.QueueEntryCreateDto
import org.bukkit.Bukkit

object QueueEntryCreateMapper {

    // From lobby service
    fun fromLobby(lobby: Lobby): QueueEntryCreate {
        return QueueEntryCreate(
            serverSlug = Config.getServerSlug(),
            players = lobby.getParticipants().map { QueueEntryPlayerMapper.fromLobby(it) },
            gameType = lobby.getLobbySettings().gameType,
            transferIP = if (Bukkit.getServer().isAcceptingTransfers && Bukkit.getIp() != "") "${Bukkit.getIp()}:${Bukkit.getPort()}" else null,
        )
    }

    // Send to backend
    fun toDto(queueEntryCreate: QueueEntryCreate): QueueEntryCreateDto {
        return QueueEntryCreateDto(
            serverSlug = queueEntryCreate.serverSlug,
            players = queueEntryCreate.players.map { QueueEntryPlayerMapper.toDto(it) },
            gameType = queueEntryCreate.gameType,
            transferIP = queueEntryCreate.transferIP,
        )
    }
}