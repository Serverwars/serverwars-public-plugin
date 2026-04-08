package net.serverwars.sunsetPlugin.commands.queue

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService

object CommandQueueEnter {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        QueueService.enterQueue(ctx.source.sender)
        return Command.SINGLE_SUCCESS
    }
}