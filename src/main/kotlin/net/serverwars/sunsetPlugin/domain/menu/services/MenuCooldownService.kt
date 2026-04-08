package net.serverwars.sunsetPlugin.domain.menu.services

import net.serverwars.sunsetPlugin.Main
import org.bukkit.Bukkit
import java.util.UUID

object MenuCooldownService {

    private const val DEFAULT_COOLDOWN_IN_TICKS = 8L
    private val playerUuidsInCooldown = mutableMapOf<UUID, Int>()

    fun hasCooldown(uuid: UUID): Boolean = this.playerUuidsInCooldown.containsKey(uuid)

    fun addCooldown(uuid: UUID, cooldown: Long = DEFAULT_COOLDOWN_IN_TICKS) {
        this.playerUuidsInCooldown[uuid]?.let { Bukkit.getScheduler().cancelTask(it) }

        this.playerUuidsInCooldown[uuid] = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.inst, Runnable {
            this.playerUuidsInCooldown.remove(uuid)
        }, cooldown).taskId
    }

}