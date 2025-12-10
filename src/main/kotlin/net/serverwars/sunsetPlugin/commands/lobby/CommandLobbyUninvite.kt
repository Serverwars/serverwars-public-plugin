package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyUninvite {

    fun run(ctx: CommandContext<CommandSourceStack>, uninvitee: Player): Int {
        val uninviter = ctx.source.sender
        if (uninviter !is Player) {
            uninviter.sendTranslatedMessage("command.lobby.uninvite.error.uninviter_not_a_player")
            return 0
        }

        try {
            val lobby = LobbyService.revokeLobbyInvitation(uninvitee.uniqueId)
            lobby.sendMessage("command.lobby.uninvite.success.notify_lobby", uninvitee.name, uninviter.name)
            uninviter.sendTranslatedMessage("command.lobby.uninvite.success", uninvitee.name)
            uninvitee.sendTranslatedMessage("command.lobby.uninvite.success.uninvitee_receive", uninviter.name)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            uninviter.sendTranslatedMessage(error.key, uninvitee.name)
            return 0
        }

    }
}