package net.serverwars.sunsetPlugin.domain.menu.listeners

import net.serverwars.sunsetPlugin.domain.menu.models.menu.Menu
import net.serverwars.sunsetPlugin.domain.menu.services.MenuCooldownService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object MenuListener: Listener {

    @EventHandler
    fun onClickItemInMenu(event: InventoryClickEvent) {
        val inv = event.inventory.holder as? Menu<*>? ?: return

        event.isCancelled = true

        // Check if player is in cooldown, add cooldown if not
        if (MenuCooldownService.hasCooldown(event.whoClicked.uniqueId)) return
        MenuCooldownService.addCooldown(event.whoClicked.uniqueId)

        inv.onClick(event)
    }

}