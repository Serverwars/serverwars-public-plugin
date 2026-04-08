package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.util.playInventoryMenuItemClickSound
import org.bukkit.NamespacedKey
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

abstract class MenuItem(
    val permission: String? = null,
) {
    companion object {
        val key = NamespacedKey(Main.inst, "menu_item")
    }

    abstract fun getItem(viewer: HumanEntity): ItemStack

    open fun onClick(humanEntity: HumanEntity) {
        playInventoryMenuItemClickSound(humanEntity)
    }

    fun hasPermission(viewer: HumanEntity): Boolean = this.permission?.let { viewer.hasPermission(it) } ?: true
}