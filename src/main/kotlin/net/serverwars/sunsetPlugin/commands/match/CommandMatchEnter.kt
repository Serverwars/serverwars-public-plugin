package net.serverwars.sunsetPlugin.commands.match

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
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
        sendPlayerToMatch(joiner)
        return Command.SINGLE_SUCCESS
    }
}