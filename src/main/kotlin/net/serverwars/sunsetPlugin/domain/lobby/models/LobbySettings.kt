package net.serverwars.sunsetPlugin.domain.lobby.models

data class LobbySettings(
    val size: Int,
    val accessType: LobbyAccessType,
    val gameType: String,
) {
    companion object {
        fun create(size: Int, accessType: LobbyAccessType, gameType: String): LobbySettings {
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

    fun withGameType(gameType: String): LobbySettings {
        return this.copy(gameType = gameType)
    }
}