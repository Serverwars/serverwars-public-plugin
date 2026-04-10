package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.util.thenRunSync
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object QueueLeaveMenuItem : MenuItem(
    permission = "serverwars.commands.queue.leave",
) {
    val material = Material.ENDER_EYE

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(this.material)

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><gold><bold>In queue..."))
            if (hasPermission(viewer) && !hasMatch()) {
                meta.lore(
                    listOf(
                        toItemText("<shadow:#A35700:0.75><white>Click to leave queue."),
                    )
                )
            }
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity) || hasMatch()) return
        super.onClick(humanEntity)

        QueueService.leaveQueue(humanEntity).thenRunSync { LobbyMenu.updateForAll() }
    }
}