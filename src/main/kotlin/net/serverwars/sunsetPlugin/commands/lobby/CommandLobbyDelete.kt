package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage

object CommandLobbyDelete {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        if (!LobbyService.lobbyExists()) {
            ctx.source.sender.sendTranslatedMessage("command.lobby.delete.error.no_lobby")
            return 0
        }
        ctx.source.sender.sendTranslatedMessage("command.lobby.delete.error.warning")
        return Command.SINGLE_SUCCESS
    }
}