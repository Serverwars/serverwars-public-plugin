package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate

import kotlinx.serialization.Serializable
import net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer.QueueEntryPlayerDto

@Serializable
data class QueueEntryCreateDto(
    val serverSecret: String,
    val players: List<QueueEntryPlayerDto>,
    val gameType: String,
    val transferIP: String?,
)