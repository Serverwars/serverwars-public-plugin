package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyKick {

    fun run(ctx: CommandContext<CommandSourceStack>, kicked: Player): Int {
        try {
            val lobby = LobbyService.kickParticipantFromLobby(Participant(kicked.uniqueId, kicked.name))
            ctx.source.sender.sendTranslatedMessage("command.lobby.kick.success", kicked.name)
            lobby.sendMessage("command.lobby.kick.success.notify_lobby", kicked.name, lobby.getParticipantAmount(), lobby.getLobbySettings().size)
            kicked.sendTranslatedMessage("command.lobby.kick.success.notify_kickee")
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, kicked.name)
            return 0
        }
    }
}