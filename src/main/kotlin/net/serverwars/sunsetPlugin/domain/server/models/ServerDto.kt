package net.serverwars.sunsetPlugin.domain.server.models

import kotlinx.serialization.Serializable

@Serializable
data class ServerDto(
    val serverUuid: String,
    val slug: String,
)
