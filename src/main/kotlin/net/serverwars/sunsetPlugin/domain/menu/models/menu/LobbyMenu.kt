package net.serverwars.sunsetPlugin.domain.menu.models.menu

import net.kyori.adventure.text.Component
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.domain.menu.models.menuitem.*
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity

class LobbyMenu(
    viewer: HumanEntity,
) : Menu<MenuItem>(
    viewer = viewer,
    size = 54,
    title = Component.text("Serverwar"),
    menuItems = run {
        val participantItemSlots = listOf(
            20, 21, 22, 23, 24,
            29, 30, 31, 32, 33,
        )
        val result = mutableMapOf<Int, MenuItem>()
        val lobby = LobbyService.getLobbyCopy()!!
        val matchLoading = MatchService.hasMatch()

        val participants = lobby.getParticipants()
        participants.forEachIndexed { index, participant ->
            result[participantItemSlots[index]] = ParticipantKickMenuItem(participant)
        }
        if (
            lobby.getParticipantAmount() < Lobby.MAX_LOBBY_SIZE &&
            ParticipantJoinMenuItem.hasPermission(viewer) &&
            LobbyService.getLobbyCopy()?.hasParticipant(Participant.create(viewer.uniqueId)) == false &&
            !matchLoading
        ) {
            result[participantItemSlots[lobby.getParticipantAmount()]] = ParticipantJoinMenuItem
        }

        result[4] = if (QueueService.isInQueue()) QueueLeaveMenuItem else QueueEnterMenuItem
        result[8] = if (matchLoading) FillerMenuItem else DeleteLobbyMenuItem
        result[26] = GameMenuItem(lobby.getLobbySettings().gameType)
        result[35] = AccessTypeMenuItem(lobby.getLobbySettings().accessType)

        result
    },
    fillerSlots = listOf(
        0, 1, 2, 3, 5, 6, 7,
        9, 10, 11, 12, 13, 14, 15, 16, 17,
        18, 19, 25,
        27, 28, 34, 35,
        36, 37, 38, 39, 40, 41, 42, 43, 44,
        45, 46, 47, 48, 49, 50, 51, 52, 53,
    )
) {
    companion object {
        fun createFor(humanEntity: HumanEntity) {
            val lobby = LobbyService.getLobbyCopy()
            if (lobby == null) {
                humanEntity.sendTranslatedMessage("command.error.no_lobby")
                return
            }

            LobbyMenu(humanEntity).open()
        }

        fun updateForAll() {
            Bukkit.getOnlinePlayers()
                .filter { player -> player.openInventory.topInventory.holder is LobbyMenu }
                .forEach { player -> LobbyMenu(player).open() }
        }

        fun closeForAll() {
            Bukkit.getOnlinePlayers()
                .filter { player -> player.openInventory.topInventory.holder is LobbyMenu }
                .forEach { player -> player.closeInventory() }
        }

        fun visualizeCooldownForAll(material: Material, cooldownInTicks: Int) {
            Bukkit.getOnlinePlayers()
                .filter { player -> player.openInventory.topInventory.holder is LobbyMenu }
                .forEach { player -> player.setCooldown(material, cooldownInTicks) }
        }
    }
}