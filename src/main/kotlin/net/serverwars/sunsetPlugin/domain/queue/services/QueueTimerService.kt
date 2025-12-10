package net.serverwars.sunsetPlugin.domain.queue.services

import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.util.now
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object QueueTimerService {
    private val tasks: MutableMap<UUID, Pair<Lobby, BukkitTask>> = ConcurrentHashMap()

    fun startTimer(lobby: Lobby, queueUuid: UUID) {
        if (tasks.containsKey(queueUuid)) return

        val startTime = now()
        val task = Bukkit.getScheduler().runTaskTimer(
            Main.inst,
            Runnable {
                val timeInQueue = (now() - startTime) / 1000
                lobby.sendActionBarMessage("queue.time_in_queue.action_bar", formatTime(timeInQueue))
            },
            0L, 20L // Run instantly, repeat every 20 ticks (1 second)
        )

        tasks[queueUuid] = Pair(lobby, task)
    }

    fun stopTimer(queueUuid: UUID): Lobby? {
        val taskLobby = tasks.remove(queueUuid)
        taskLobby?.second?.cancel()
        return taskLobby?.first
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

}