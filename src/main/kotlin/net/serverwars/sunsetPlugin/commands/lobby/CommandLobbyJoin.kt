package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyJoinOperation
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyKickOperation
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
        return run(ctx, joiner)
    }

    fun run(ctx: CommandContext<CommandSourceStack>, joiner: Player): Int {
        val operation = LobbyJoinOperation(
            audience = ctx.source.sender,
            executor = ctx.source.sender as? Player,
            joiner = joiner
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}