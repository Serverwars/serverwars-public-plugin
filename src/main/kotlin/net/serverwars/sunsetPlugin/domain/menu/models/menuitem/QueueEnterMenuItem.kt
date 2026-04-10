package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.util.thenRunSync
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object QueueEnterMenuItem : MenuItem(
    permission = "serverwars.commands.queue.enter"
) {
    val material = Material.ENDER_PEARL

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(this.material)

        result.editMeta { meta ->
            val displayName = if (hasPermission(viewer)) "Enter queue" else "Waiting for players..."
            meta.displayName(toItemText("<shadow:#A35700:0.75><gold><bold>$displayName"))
            if (hasPermission(viewer)) {
                meta.lore(
                    listOf(
                        toItemText("<shadow:#A35700:0.75><white>Click to enter the match making queue."),
                    )
                )
            }
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity)) return
        super.onClick(humanEntity)

        QueueService.enterQueue(humanEntity).thenRunSync { LobbyMenu.updateForAll() }
    }
}