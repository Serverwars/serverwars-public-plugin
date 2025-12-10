package net.serverwars.sunsetPlugin.commands.manager

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import net.serverwars.sunsetPlugin.commands.CommandServerwars

object CommandManager {

    fun registerCommands(commands: ReloadableRegistrarEvent<Commands>) {
        commands.registrar().register(CommandServerwars.command)
    }
}