package net.serverwars.sunsetPlugin.domain.server.models

import java.util.UUID

data class Server(
    val serverUuid: UUID,
    val slug: String,
)
