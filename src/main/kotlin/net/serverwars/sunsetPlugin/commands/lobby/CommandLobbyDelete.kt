package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyDeleteOperation
import org.bukkit.entity.Player

object CommandLobbyDelete {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        val operation = LobbyDeleteOperation(
            audience = ctx.source.sender,
            executor = ctx.source.sender as? Player,
            confirmed = false,
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}