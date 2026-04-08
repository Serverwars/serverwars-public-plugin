package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

object FillerMenuItem: MenuItem() {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(Material.GRAY_STAINED_GLASS_PANE)

        result.editMeta { meta ->
            meta.displayName(Component.empty())
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {} // Override to do nothing
}