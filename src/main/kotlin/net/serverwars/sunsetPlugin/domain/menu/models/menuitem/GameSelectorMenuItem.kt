package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack

data class GameSelectorMenuItem(
    private val gameType: GameServerType,
): SelectorMenuItem<GameServerType>(
    value = gameType,
    permission = "serverwars.commands.lobby.set.game",
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = this.gameType.getItem()

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>${this.gameType.name.replaceFirstChar { it.uppercase() }}"))
            meta.lore(
                this.gameType.description.map { toItemText("<shadow:#A35700:0.75><white>$it") }
            )
        }

        return result
    }
}