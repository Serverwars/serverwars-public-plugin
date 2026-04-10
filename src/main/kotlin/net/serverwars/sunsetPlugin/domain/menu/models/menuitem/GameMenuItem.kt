package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbySetGameTypeOperation
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyGameTypeMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class GameMenuItem(
    private val gameType: GameServerType,
): MenuItem(
    permission = "serverwars.commands.lobby.set.game"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = this.gameType.getItem()

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>Game: <gold>${this.gameType.name.replaceFirstChar { it.uppercase() }}"))
            if (hasPermission(viewer) && !hasMatch())
            meta.lore(
                listOf(
                    toItemText("<shadow:#A35700:0.75><white>Click to choose a different game."),
                )
            )
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity) || hasMatch()) return
        super.onClick(humanEntity)

        LobbyGameTypeMenu.createFor(humanEntity) { pickedGameType ->
            val operation = LobbySetGameTypeOperation(
                audience = humanEntity,
                executor = humanEntity as? Player,
                gameType = pickedGameType,
            )
            operation.execute()

            // Update lobby menu for all players currently looking at it
            LobbyMenu.updateForAll()

            // Open lobby menu
            LobbyMenu.createFor(humanEntity)
        }
    }
}