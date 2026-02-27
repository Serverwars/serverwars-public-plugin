package net.serverwars.sunsetPlugin.domain.lobby.models

data class LobbySettings(
    val accessType: LobbyAccessType,
    val gameType: String,
) {
    companion object {
        fun create(accessType: LobbyAccessType, gameType: String): LobbySettings {
            return LobbySettings(
                accessType = accessType,
                gameType = gameType,
            )
        }
    }

    fun withAccessType(accessType: LobbyAccessType): LobbySettings {
        return this.copy(accessType = accessType)
    }

    fun withGameType(gameType: String): LobbySettings {
        return this.copy(gameType = gameType)
    }
}