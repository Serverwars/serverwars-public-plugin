package net.serverwars.sunsetPlugin.domain.queue.models.queueentrycreate

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import java.util.*

data class QueueEntryCreate(
    val serverSlug: String,
    val playerUuids: List<UUID>,
    val gameType: GameServerType,
    val transferIP: String?,
)