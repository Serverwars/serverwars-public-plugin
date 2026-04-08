package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.domain.menu.models.menu.LobbyMenu
import net.serverwars.sunsetPlugin.domain.menu.models.stepper.LobbyCreatorMenuStepper
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import net.serverwars.sunsetPlugin.util.playOpenMenuSound
import org.bukkit.entity.Player

object CommandLobby {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.sender
        if (player !is Player) {
            ctx.source.sender.sendTranslatedMessage("command.lobby.error.not_a_player")
            return 0
        }

        if (!player.hasPermission("serverwars.commands.lobby.menu")) {
            ctx.source.sender.sendTranslatedMessage("command.error.no_permission")
            return 0
        }

        if (LobbyService.lobbyExists()) {
            LobbyMenu.createFor(player)
        } else {
            if (!player.hasPermission("serverwars.commands.lobby.create")) {
                ctx.source.sender.sendTranslatedMessage("command.error.no_lobby")
                return 0
            }
            LobbyCreatorMenuStepper(player).open()
        }
        playOpenMenuSound(player)

        return Command.SINGLE_SUCCESS
    }
}