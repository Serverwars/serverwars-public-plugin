package net.serverwars.sunsetPlugin.domain.gameserver.models

import kotlinx.serialization.Serializable

@Serializable
data class GameServerTypesDto(
    val types: List<String>,
)
