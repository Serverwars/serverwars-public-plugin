package net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype

import kotlinx.serialization.Serializable

@Serializable
data class GameServerTypeDto(
    val name: String,
    val material: String,
    val description: List<String>
)
