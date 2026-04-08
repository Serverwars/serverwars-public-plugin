package net.serverwars.sunsetPlugin.listeners

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.menu.listeners.MenuListener
import org.bukkit.Bukkit

object EventListenerManager {

    fun initialize() {
        setOf(
            PlayerQuitListener,
            MenuListener,
        ).forEach { listener -> Bukkit.getPluginManager().registerEvents(listener, Main.inst) }
    }

}