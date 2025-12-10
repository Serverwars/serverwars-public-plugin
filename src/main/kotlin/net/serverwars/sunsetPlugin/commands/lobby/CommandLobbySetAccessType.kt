package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage

object CommandLobbySetAccessType {

    fun run(ctx: CommandContext<CommandSourceStack>, accessType: LobbyAccessType): Int {
        try {
            val lobby = LobbyService.updateLobbyAccessType(value = accessType)
            ctx.source.sender.sendTranslatedMessage("command.lobby.set.access_type.success", accessType.value)
            lobby.sendMessage("command.lobby.set.access_type.success.notify_lobby", accessType.value)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, accessType)
            return 0
        }
    }
}