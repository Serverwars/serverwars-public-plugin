package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyJoinOperation
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ParticipantJoinMenuItem : MenuItem(
    permission = "serverwars.commands.lobby.join"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(Material.ACACIA_BUTTON)

        result.editMeta { meta ->
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>Join Serverwar"))
            meta.lore(
                listOf(
                    toItemText("<shadow:#A35700:0.75><white>Click to join the Serverwar."),
                )
            )
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity)) return
        super.onClick(humanEntity)

        // Join player
        val operation = LobbyJoinOperation(
            audience = humanEntity,
            executor = humanEntity as Player,
            joiner = humanEntity,
        )
        operation.execute()

        LobbyMenu.updateForAll()
        LobbyMenu.createFor(humanEntity)
    }
}