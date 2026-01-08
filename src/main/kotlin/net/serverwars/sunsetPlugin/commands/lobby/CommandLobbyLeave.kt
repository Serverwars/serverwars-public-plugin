package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.UpdateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.Participant
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.entity.Player

object CommandLobbyLeave {

    fun self(ctx: CommandContext<CommandSourceStack>): Int {
        val leaver = ctx.source.sender
        if (leaver !is Player) {
            ctx.source.sender.sendTranslatedMessage("command.lobby.leave.self.error.not_a_player")
            return 0
        }
        return run(leaver)
    }

    private fun run(leaver: Player): Int {
        try {
            val lobby = LobbyService.participantLeaveLobby(Participant(leaver.uniqueId, leaver.name))
            leaver.sendTranslatedMessage("command.lobby.leave.success")
            lobby.sendMessage("command.lobby.leave.success.notify_lobby", leaver.name, lobby.getParticipantAmount(), lobby.getLobbySettings().size)
            return Command.SINGLE_SUCCESS
        } catch (error: UpdateLobbyException) {
            leaver.sendTranslatedMessage(error.key, leaver.name)
            return 0
        }

    }
}