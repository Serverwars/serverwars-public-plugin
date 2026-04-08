package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyKickOperation
import org.bukkit.entity.Player

object CommandLobbyKick {

    fun run(ctx: CommandContext<CommandSourceStack>, kicked: Player): Int {
        val operation = LobbyKickOperation(
            audience = ctx.source.sender,
            executor = ctx.source.sender as? Player,
            kicked = kicked
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}