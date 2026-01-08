package net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer

import java.util.UUID

data class QueueEntryPlayer(
    val playerUuid: UUID,
    val name: String,
)
