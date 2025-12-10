package net.serverwars.sunsetPlugin.listeners

import net.serverwars.sunsetPlugin.Main
import org.bukkit.Bukkit

object EventListenerManager {

    fun initialize() {
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener, Main.inst)
    }

}