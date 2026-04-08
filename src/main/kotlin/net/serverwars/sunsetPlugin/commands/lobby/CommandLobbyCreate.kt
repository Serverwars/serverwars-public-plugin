package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbySettings
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyCreateOperation
import org.bukkit.entity.Player

object CommandLobbyCreate {

    fun run(ctx: CommandContext<CommandSourceStack>, accessType: LobbyAccessType, gameType: GameServerType): Int {
        val operation = LobbyCreateOperation(
            audience = ctx.source.sender,
            lobbySettings = LobbySettings(
                accessType = accessType,
                gameType = gameType
            ),
            executor = ctx.source.sender as? Player
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}