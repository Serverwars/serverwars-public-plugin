package net.serverwars.sunsetPlugin.commands.match

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.match.services.MatchService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.runAsync
import net.serverwars.sunsetPlugin.util.runSync
import net.serverwars.sunsetPlugin.util.sendPlayerToMatch
import org.bukkit.entity.Player

object CommandMatchEnter {

    fun self(ctx: CommandContext<CommandSourceStack>): Int {
        val joiner = ctx.source.sender
        if (joiner !is Player) {
            ctx.source.sender.sendTranslatedMessage("command.match.join.self.error.not_a_player")
            return 0
        }
        return run(joiner)
    }

    fun run(joiner: Player): Int {
        runAsync {
            if (MatchService.checkInMatch()) {
                runSync { sendPlayerToMatch(joiner) }
            } else {
                joiner.sendTranslatedMessage("command.match.join.error.not_in_match")
            }
        }
        return Command.SINGLE_SUCCESS
    }
}