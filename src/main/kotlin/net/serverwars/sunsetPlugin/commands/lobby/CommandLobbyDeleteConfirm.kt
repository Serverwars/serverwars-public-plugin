package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.DeleteLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage

object CommandLobbyDeleteConfirm {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        try {
            val lobby = LobbyService.deleteLobby()
            lobby.sendMessage("command.lobby.delete.success.notify_lobby")
            ctx.source.sender.sendTranslatedMessage("command.lobby.delete.success")
            return Command.SINGLE_SUCCESS
        } catch (error: DeleteLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, *error.args)
            return 0
        }
    }
}