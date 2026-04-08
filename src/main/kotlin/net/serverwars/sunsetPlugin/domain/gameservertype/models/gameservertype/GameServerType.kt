package net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

data class GameServerType(
    val name: String,
    val material: Material,
    val description: List<String>
) {
    fun getItem(): ItemStack {
        val item = ItemStack(material)

        item.editMeta { meta ->
            meta.addItemFlags(*ItemFlag.entries.toTypedArray())

            val leatherArmorMeta = meta as? LeatherArmorMeta
            leatherArmorMeta?.setColor(Color.ORANGE)
        }

        return item
    }
}
