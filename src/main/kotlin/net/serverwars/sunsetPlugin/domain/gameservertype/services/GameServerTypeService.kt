package net.serverwars.sunsetPlugin.domain.gameservertype.services

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.util.runAsync

object GameServerTypeService {

    val availableGameServerTypes = mutableListOf<GameServerType>()

    fun refreshAvailableGameServerTypes() {
        runAsync {
            this.availableGameServerTypes.clear()
            this.availableGameServerTypes.addAll(GameServerTypeDataAccess.getAvailableGameServerTypes().gameServerTypes)
        }
    }

    fun getGameServerTypeFromName(name: String): GameServerType? = availableGameServerTypes.find { it.name == name }
}