package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate

import net.serverwars.sunsetPlugin.domain.queue.models.queueentryplayer.QueueEntryPlayer

data class QueueEntryCreate(
    val serverSecret: String,
    val players: List<QueueEntryPlayer>,
    val gameType: String,
    val transferIP: String?,
)