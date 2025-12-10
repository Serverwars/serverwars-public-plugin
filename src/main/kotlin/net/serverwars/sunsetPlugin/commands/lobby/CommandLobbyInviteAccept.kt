package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyInviteAccept {

    fun self(ctx: CommandContext<CommandSourceStack>): Int {
        val invitee = ctx.source.sender
        if (invitee !is Player) {
            ctx.source.sender.sendTranslatedMessage("command.lobby.invite.accept.self.error.not_a_player")
            return 0
        }
        return run(invitee)
    }

    private fun run(invitee: Player): Int {
        try {
            val lobby = LobbyService.acceptLobbyInvitation(invitee.uniqueId)
            invitee.sendTranslatedMessage("command.lobby.invite.accept.success")
            lobby.sendMessageExcluding(invitee.uniqueId, "command.lobby.invite.accept.success.notify_lobby", invitee.name)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            invitee.sendTranslatedMessage(error.key, invitee.name)
            return 0
        }

    }
}