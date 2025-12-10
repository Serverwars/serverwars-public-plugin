package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyInvite {

    fun run(ctx: CommandContext<CommandSourceStack>, invitee: Player): Int {
        val inviter = ctx.source.sender
        if (inviter !is Player) {
            inviter.sendTranslatedMessage("command.lobby.invite.error.inviter_not_a_player")
            return 0
        }

        try {
            val lobby = LobbyService.createLobbyInvitation(inviter.uniqueId, invitee.uniqueId)
            inviter.sendTranslatedMessage("command.lobby.invite.success", invitee.name)
            lobby.sendMessage("command.lobby.invite.success.notify_lobby", invitee.name, inviter.name)
            invitee.sendTranslatedMessage("command.lobby.invite.success.invitee_receive", inviter.name)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            inviter.sendTranslatedMessage(error.key, invitee.name)
            return 0
        }
    }
}