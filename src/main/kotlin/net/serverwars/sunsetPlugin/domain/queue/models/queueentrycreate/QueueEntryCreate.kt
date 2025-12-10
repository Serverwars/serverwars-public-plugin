package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate

import java.util.*

data class QueueEntryCreate(
    val serverSlug: String,
    val playerUuids: List<UUID>,
    val gameType: String,
    val transferIP: String?,
)