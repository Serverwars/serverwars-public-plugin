package net.serverwars.sunsetPlugin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.future
import net.serverwars.sunsetPlugin.Main
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object PluginCoroutines {
    private val executor: ExecutorService = Executors.newFixedThreadPool(1)
    private val job = SupervisorJob()
    val scope = CoroutineScope(executor.asCoroutineDispatcher() + job)
}

fun <T> runAsync(runnable: suspend () -> T): CompletableFuture<T> {
    return PluginCoroutines.scope.future {
        runnable()
    }
}

fun runSync(runnable: () -> Unit) {
    Bukkit.getScheduler().runTask(Main.inst, Runnable { runnable() })
}