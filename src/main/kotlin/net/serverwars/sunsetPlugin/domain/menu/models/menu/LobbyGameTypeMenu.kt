package net.serverwars.sunsetPlugin.domain.menu.models.menu

import net.kyori.adventure.text.Component
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.gameservertype.services.GameServerTypeService
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.*
import org.bukkit.entity.HumanEntity

class LobbyGameTypeMenu(
    viewer: HumanEntity,
    onSelect: (GameServerType) -> Unit
) : SelectorMenu<GameServerType>(
    viewer = viewer,
    size = 27,
    title = Component.text("What game?"),
    menuItems = run {
        val gameTypeSlots = listOf(10, 11, 12, 13, 14, 15, 16)
        GameServerTypeService.availableGameServerTypes.mapIndexed { index, gameServerType ->
            gameTypeSlots[index] to GameSelectorMenuItem(gameServerType)
        }.toMap()
    },
    fillerSlots = listOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8,
        9, 17,
        18, 19, 20, 21, 22, 23, 24, 25, 26,
    ),
    onSelect = onSelect,
) {
    companion object {
        fun createFor(humanEntity: HumanEntity, onSelect: (GameServerType) -> Unit = {}) = LobbyGameTypeMenu(
            viewer = humanEntity,
            onSelect = onSelect,
        ).open()
    }
}