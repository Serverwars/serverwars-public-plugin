package net.serverwars.sunsetPlugin.util

import net.serverwars.sunsetPlugin.config.Config
import net.serverwars.sunsetPlugin.domain.server.services.ServerService
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun sendPlayerToMatch(player: Player) {
    val command = "transfer ${ServerService.getServerSlug()}.${Config.getServerwarsMinecraftServerIP()} 25565 ${player.name}"
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)
}