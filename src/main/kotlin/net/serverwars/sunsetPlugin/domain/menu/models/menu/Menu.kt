package net.serverwars.sunsetPlugin.domain.menu.models.menu

import net.kyori.adventure.text.Component
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.FillerMenuItem
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.MenuItem
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class Menu<T: MenuItem>(
    val viewer: HumanEntity,
    val size: Int = 27,
    val title: Component,
    val menuItems: Map<Int, T>,
    val fillerSlots: List<Int>,
) : InventoryHolder, Listener {

    private val inventory = Bukkit.createInventory(
        this,
        this.size,
        this.title
    )

    init {
        this.fillerSlots.forEach { slot -> this.inventory.setItem(slot, FillerMenuItem.getItem(this.viewer)) }
        this.menuItems.forEach { (slot, item) -> this.inventory.setItem(slot, item.getItem(this.viewer)) }
    }

    override fun getInventory(): Inventory = this.inventory

    open fun onClick(event: InventoryClickEvent) {
        this.menuItems[event.rawSlot]?.onClick(event.whoClicked)
    }

    fun open() {
        this.viewer.openInventory(this.inventory)
    }
}