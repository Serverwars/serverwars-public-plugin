package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbySetAccessTypeOperation
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyAccessTypeMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class AccessTypeMenuItem(
    private val currentAccessType: LobbyAccessType
) : MenuItem(
    permission = "serverwars.commands.lobby.set.access"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val material = when (currentAccessType) {
            LobbyAccessType.INVITE_ONLY -> Material.IRON_DOOR
            LobbyAccessType.OPEN -> Material.OAK_DOOR
        }
        val result = ItemStack(material)

        result.editMeta { meta ->
            val displayName = when (currentAccessType) {
                LobbyAccessType.INVITE_ONLY -> "Invite only"
                LobbyAccessType.OPEN -> "Open"
            }
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>Access type: <gold>$displayName"))

            if (hasPermission(viewer)) {
                meta.lore(
                    listOf(
                        toItemText("<shadow:#A35700:0.75><white>Click to choose who can join the Serverwar."),
                    )
                )
            }
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity)) return
        super.onClick(humanEntity)

        LobbyAccessTypeMenu.createFor(humanEntity) { pickedAccessType ->
            val operation = LobbySetAccessTypeOperation(
                audience = humanEntity,
                executor = humanEntity as? Player,
                accessType = pickedAccessType,
            )
            operation.execute()

            // Update lobby menu for all players currently looking at it
            LobbyMenu.updateForAll()

            // Open lobby menu
            LobbyMenu.createFor(humanEntity)
        }
    }
}