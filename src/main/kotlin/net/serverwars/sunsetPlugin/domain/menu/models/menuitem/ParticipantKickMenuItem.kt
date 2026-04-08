package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyKickOperation
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.domain.menu.models.menu.Menu
import net.serverwars.sunsetPlugin.util.toItemText
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

data class ParticipantKickMenuItem(
    val participant: Participant
) : MenuItem(
    permission = "serverwars.commands.lobby.kick"
) {

    override fun getItem(viewer: HumanEntity): ItemStack {
        val result = ItemStack(Material.PLAYER_HEAD)

        result.editMeta { itemMeta ->
            val meta = itemMeta as SkullMeta
            meta.displayName(toItemText("<shadow:#A35700:0.75><yellow>${this.participant.name}"))
            if (hasPermission(viewer)) {
                meta.lore(
                    listOf(
                        toItemText("<shadow:#A35700:0.75><white>Click to kick player from the Serverwar."),
                    )
                )
            }
            meta.owningPlayer = Bukkit.getOfflinePlayer(this.participant.playerUuid)
        }

        return result
    }

    override fun onClick(humanEntity: HumanEntity) {
        if (!hasPermission(humanEntity)) return
        super.onClick(humanEntity)

        // Kick player
        val kickedPlayer = participant.getAsPlayer()
        val operation = LobbyKickOperation(
            audience = humanEntity,
            executor = humanEntity as? Player,
            kicked = kickedPlayer,
        )
        operation.execute()

        // Close the kicked player's open menu if present
        if (kickedPlayer.openInventory.topInventory.holder is Menu<*>) {
            kickedPlayer.closeInventory()
        }

        LobbyMenu.updateForAll()
    }
}