package net.serverwars.sunsetPlugin

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.serverwars.sunsetPlugin.commands.manager.CommandManager

class PluginBootstrapper : PluginBootstrap {

    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, { commands ->
            CommandManager.registerCommands(commands)
        })
    }
}