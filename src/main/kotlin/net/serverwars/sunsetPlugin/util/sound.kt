package net.serverwars.sunsetPlugin.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.Audience.audience
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import org.bukkit.Bukkit
import org.bukkit.Sound

fun playSetLobbySound(lobby: Lobby) {
    playSound(
        type = Sound.UI_BUTTON_CLICK,
        volume = 0.5f,
        target = lobby.getLobbyAudience()
    )
}

fun playPlayerJoinsLobbySound(lobby: Lobby) {
    playSound(
        type = Sound.ENTITY_ARROW_HIT_PLAYER,
        pitch = 2f,
        target = lobby.getLobbyAudience()
    )
}

fun playPlayerLeavesLobbySound(lobby: Lobby) {
    playSound(
        type = Sound.UI_TOAST_OUT,
        volume = 5f,
        target = lobby.getLobbyAudience()
    )
}

fun playPlayerKickedFromLobbySound(lobby: Lobby) {
    playSound(
        type = Sound.UI_TOAST_OUT,
        volume = 5f,
        target = lobby.getLobbyAudience()
    )
}

fun playCreateInviteSound(lobby: Lobby) {
    playSound(
        type = Sound.UI_TOAST_IN,
        volume = 5f,
        target = lobby.getLobbyAudience()
    )
}

fun playRevokeInviteSound(lobby: Lobby) {
    playSound(
        type = Sound.UI_TOAST_OUT,
        volume = 5f,
        target = lobby.getLobbyAudience()
    )
}

fun playEnterQueueSound(lobby: Lobby) {
    playSound(
        type = Sound.BLOCK_BEACON_ACTIVATE,
        target = lobby.getLobbyAudience()
    )
}

fun playLeaveQueueSound(lobby: Lobby) {
    playSound(
        type = Sound.BLOCK_BEACON_DEACTIVATE,
        target = lobby.getLobbyAudience()
    )
}

fun playMatchFoundSound(lobby: Lobby) {
    playSound(
        type = Sound.BLOCK_END_PORTAL_SPAWN,
        volume = 0.5f,
        target = lobby.getLobbyAudience()
    )
    playSound(
        type = Sound.ITEM_GOAT_HORN_SOUND_2,
        target = lobby.getLobbyAudience(),
        delay = 12L,
    )
}

private fun playSound(
    type: net.kyori.adventure.sound.Sound.Type,
    pitch: Float = 1f,
    volume: Float = 1f,
    delay: Long? = null,
    target: Audience = audience(Bukkit.getServer().onlinePlayers)
    ) {
    val sound = net.kyori.adventure.sound.Sound.sound()
        .source(net.kyori.adventure.sound.Sound.Source.MASTER)
        .type(type)
        .pitch(pitch)
        .volume(volume)
        .build()

    if (delay == null) {
        target.playSound(sound)
    } else {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.inst, {
            target.playSound(sound)
        }, delay)
    }
}
