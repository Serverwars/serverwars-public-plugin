package net.serverwars.sunsetPlugin.domain.lobby.models

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType

data class LobbySettings(
    val accessType: LobbyAccessType,
    val gameType: GameServerType,
) {
    companion object {
        fun create(accessType: LobbyAccessType, gameType: GameServerType) = LobbySettings(
            accessType = accessType,
            gameType = gameType,
        )
    }

    fun withAccessType(accessType: LobbyAccessType): LobbySettings {
        return this.copy(accessType = accessType)
    }

    fun withGameType(gameType: GameServerType): LobbySettings {
        return this.copy(gameType = gameType)
    }
}