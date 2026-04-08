package net.serverwars.sunsetPlugin.domain.menu.models.menu

import net.kyori.adventure.text.Component
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.SelectorMenuItem
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent

abstract class SelectorMenu<T>(
    viewer: HumanEntity,
    size: Int = 27,
    title: Component,
    menuItems: Map<Int, SelectorMenuItem<T>>,
    fillerSlots: List<Int>,
    val onSelect: (T) -> Unit
): Menu<SelectorMenuItem<T>>(viewer, size, title, menuItems, fillerSlots) {

    override fun onClick(event: InventoryClickEvent) {
        super.onClick(event)

        val selectorMenuItem = this.menuItems[event.rawSlot] ?: return

        this.onSelect(selectorMenuItem.value)
    }
}