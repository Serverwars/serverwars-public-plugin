package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage

object CommandLobbySetSize {

    fun run(ctx: CommandContext<CommandSourceStack>, size: Int): Int {
        try {
            val lobby = LobbyService.updateLobbySize(value = size)
            ctx.source.sender.sendTranslatedMessage("command.lobby.set.size.success", size)
            lobby.sendMessage("command.lobby.set.size.success.notify_lobby", size)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, size)
            return 0
        }

    }
}