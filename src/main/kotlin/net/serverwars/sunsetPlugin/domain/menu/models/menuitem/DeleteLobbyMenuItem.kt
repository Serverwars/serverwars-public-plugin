package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyDeleteOperation
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DeleteLobbyMenuItem : MenuItem(
    permission = "serverwars.commands.lobby.delete"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        if (!hasPermission(viewer)) return FillerMenuItem.getItem(viewer)
        val result = ItemStack(Material.BARRIER)

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>Delete lobby"))
            meta.lore(
                listOf(
                    toItemText("<shadow:#A35700:0.75><white>Click to delete lobby."),
                )
            )
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity)) return
        super.onClick(humanEntity)

        val operation = LobbyDeleteOperation(
            audience = humanEntity,
            executor = humanEntity as? Player,
            confirmed = false,
        )
        operation.execute()
        humanEntity.closeInventory()
    }
}