package net.serverwars.sunsetPlugin.commands.queue

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.queue.exceptions.LeaveQueueException
import net.serverwars.sunsetPlugin.domain.queue.services.QueueService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.runAsync

object CommandQueueLeave {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        runAsync {
            try {
                QueueService.leaveQueue()
            } catch(error: LeaveQueueException) {
                ctx.source.sender.sendTranslatedMessage(error.key, *error.args)
            }
        }

        return Command.SINGLE_SUCCESS
    }
}