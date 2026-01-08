package net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer

import kotlinx.serialization.Serializable

@Serializable
data class QueueEntryPlayerDto(
    val playerUuid: String,
    val name: String,
)
