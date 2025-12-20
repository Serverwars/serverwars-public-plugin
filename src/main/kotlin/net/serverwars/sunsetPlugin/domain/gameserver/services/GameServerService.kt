package net.serverwars.sunsetPlugin.domain.gameserver.services

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypes
import net.serverwars.sunsetPlugin.util.runAsync

object GameServerService {

    var availableGameServerTypes: GameServerTypes? = null

    fun initialize() {
        runAsync {
            this.availableGameServerTypes = GameServerDataAccess.getAvailableGameServerTypes()
        }
    }
}