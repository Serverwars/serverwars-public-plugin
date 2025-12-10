package net.serverwars.sunsetPlugin.domain.lobby.models

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType

data class LobbySettings(
    val size: Int,
    val accessType: LobbyAccessType,
    val gameType: GameServerType,
) {
    companion object {
        fun create(size: Int, accessType: LobbyAccessType, gameType: GameServerType): LobbySettings {
            return LobbySettings(
                size = size,
                accessType = accessType,
                gameType = gameType,
            )
        }
    }

    fun withSize(size: Int): LobbySettings {
        return this.copy(size = size)
    }

    fun withAccessType(accessType: LobbyAccessType): LobbySettings {
        return this.copy(accessType = accessType)
    }

    fun withGameType(gameType: GameServerType): LobbySettings {
        return this.copy(gameType = gameType)
    }
}