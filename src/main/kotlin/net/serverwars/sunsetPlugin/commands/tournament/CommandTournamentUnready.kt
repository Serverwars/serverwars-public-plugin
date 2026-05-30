package net.serverwars.sunsetPlugin.commands.tournament

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.TournamentParticipantService

object CommandTournamentUnready {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        TournamentParticipantService.unready(ctx.source.sender)
        return Command.SINGLE_SUCCESS
    }
}