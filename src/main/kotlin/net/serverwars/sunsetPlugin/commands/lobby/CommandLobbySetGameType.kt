package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbySetGameTypeOperation
import org.bukkit.entity.Player

object CommandLobbySetGameType {

    fun run(ctx: CommandContext<CommandSourceStack>, gameType: GameServerType): Int {
        val operation = LobbySetGameTypeOperation(
            audience = ctx.source.sender,
            executor = ctx.source.sender as? Player,
            gameType = gameType,
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}