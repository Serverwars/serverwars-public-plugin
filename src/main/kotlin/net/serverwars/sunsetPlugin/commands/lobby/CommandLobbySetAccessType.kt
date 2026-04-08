package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbyDeleteOperation
import net.serverwars.sunsetPlugin.domain.lobby.models.operations.LobbySetAccessTypeOperation
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbySetAccessType {

    fun run(ctx: CommandContext<CommandSourceStack>, accessType: LobbyAccessType): Int {
        val operation = LobbySetAccessTypeOperation(
            audience = ctx.source.sender,
            executor = ctx.source.sender as? Player,
            accessType = accessType,
        )
        val success = operation.execute()

        return if (success) Command.SINGLE_SUCCESS else 0
    }
}