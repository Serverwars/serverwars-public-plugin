package net.serverwars.sunsetPlugin.domain.menu.models.menu

import net.kyori.adventure.text.Component
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.*
import org.bukkit.entity.HumanEntity

class LobbyAccessTypeMenu(
    viewer: HumanEntity,
    onSelect: (LobbyAccessType) -> Unit,
) : SelectorMenu<LobbyAccessType>(
    viewer = viewer,
    size = 27,
    title = Component.text("Who can join?"),
    menuItems = mutableMapOf(
        11 to AccessTypeSelectorMenuItem(accessType = LobbyAccessType.OPEN),
        15 to AccessTypeSelectorMenuItem(accessType = LobbyAccessType.INVITE_ONLY),
    ),
    fillerSlots = listOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 10, 12, 13, 14, 16, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
    ),
    onSelect = onSelect,
) {
    companion object {
        fun createFor(humanEntity: HumanEntity, onSelect: (LobbyAccessType) -> Unit = {}) = LobbyAccessTypeMenu(
            viewer = humanEntity,
            onSelect = onSelect,
        ).open()
    }
}