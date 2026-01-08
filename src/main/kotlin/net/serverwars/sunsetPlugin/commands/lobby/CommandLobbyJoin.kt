package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyJoin {

    fun self(ctx: CommandContext<CommandSourceStack>): Int {
        val joiner = ctx.source.sender
        if (joiner !is Player) {
            ctx.source.sender.sendTranslatedMessage("command.lobby.join.self.error.not_a_player")
            return 0
        }
        return run(joiner)
    }

    fun run(joiner: Player): Int {
        try {
            val lobby = LobbyService.playerJoinLobby(joiner.uniqueId)
            joiner.sendTranslatedMessage("command.lobby.join.success")
            lobby.sendMessage("command.lobby.join.success.notify_lobby", joiner.name, lobby.getParticipantAmount(), lobby.getLobbySettings().size)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            joiner.sendTranslatedMessage(error.key, joiner.name)
            return 0
        }
    }
}