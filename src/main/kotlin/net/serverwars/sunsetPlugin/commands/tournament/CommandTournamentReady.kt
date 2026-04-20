package net.serverwars.sunsetPlugin.commands.tournament

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.tournamentparticipant.services.TournamentParticipantService

object CommandTournamentReady {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        TournamentParticipantService.ready(ctx.source.sender)
        return Command.SINGLE_SUCCESS
    }
}