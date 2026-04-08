package net.serverwars.sunsetPlugin.domain.menu.models.stepper

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyCreateOperation
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyAccessTypeMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyGameTypeMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

class LobbyCreatorMenuStepper(
    viewer: HumanEntity,
) : MenuStepper(viewer) {

    private lateinit var accessType: LobbyAccessType
    private lateinit var gameServerType: GameServerType

    override val steps = listOf(
        LobbyAccessTypeMenu(viewer) {
            this.accessType = it
            this.onStepComplete()
        },
        LobbyGameTypeMenu(viewer) {
            this.gameServerType = it
            this.onStepComplete()
        },
    )

    override fun onFinalStepComplete() {
        val operation = LobbyCreateOperation(
            audience = this.viewer,
            lobbySettings = LobbySettings(
                accessType = this.accessType,
                gameType = this.gameServerType,
            ),
            executor = this.viewer as? Player
        )
        val success = operation.execute()

        if (success) {
            LobbyMenu.createFor(this.viewer)
        }
    }
}
