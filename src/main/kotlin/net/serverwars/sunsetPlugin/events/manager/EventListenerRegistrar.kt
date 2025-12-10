package net.serverwars.sunsetPlugin.events.manager

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.events.PlayerQuitListener
import org.bukkit.Bukkit

object EventListenerRegistrar {

    fun registerEventListeners() {
        Bukkit.getServer().pluginManager.registerEvents(PlayerQuitListener(), Main.inst)
    }
}