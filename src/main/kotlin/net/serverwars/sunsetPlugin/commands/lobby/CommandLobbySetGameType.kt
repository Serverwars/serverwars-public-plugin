package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage

object CommandLobbySetGameType {

    fun run(ctx: CommandContext<CommandSourceStack>, gameType: String): Int {
        try {
            val lobby = LobbyService.updateLobbyGameType(value = gameType)
            ctx.source.sender.sendTranslatedMessage("command.lobby.set.game_type.success", gameType)
            lobby.sendMessage("command.lobby.set.game_type.success.notify_lobby", gameType)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, gameType)
            return 0
        }
    }
}