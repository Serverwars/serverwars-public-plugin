package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

data class AccessTypeSelectorMenuItem(
    private val accessType: LobbyAccessType,
) : SelectorMenuItem<LobbyAccessType>(
    value = accessType,
    permission = "serverwars.commands.lobby.set.game"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(this.accessType.material)

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>${this.accessType.title}"))
            if (hasPermission(viewer)) {
                meta.lore(
                    listOf(toItemText("<shadow:#A35700:0.75><white>${this.accessType.description}")),
                )
            }
        }

        return result
    }
}