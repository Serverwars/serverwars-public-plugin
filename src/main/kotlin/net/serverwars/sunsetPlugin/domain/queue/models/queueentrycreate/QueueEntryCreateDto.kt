package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate

import kotlinx.serialization.Serializable

@Serializable
data class QueueEntryCreateDto(
    val serverSlug: String,
    val playerUuids: List<String>,
    val gameType: String,
    val transferIP: String?,
)