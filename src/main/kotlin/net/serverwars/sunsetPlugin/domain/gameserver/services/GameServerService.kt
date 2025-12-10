package net.serverwars.sunsetPlugin.domain.gameserver.services

import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerTypes
import net.serverwars.sunsetPlugin.util.runAsync

object GameServerService {

    lateinit var availableGameServerTypes: GameServerTypes

    fun initialize() {
        runAsync {
            this.availableGameServerTypes = GameServerDataAccess.getAvailableGameServerTypes()
        }
    }
}