package net.serverwars.sunsetPlugin.commands.lobby

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.audience.Audience
import net.serverwars.sunsetPlugin.domain.gameserver.models.GameServerType
import net.serverwars.sunsetPlugin.domain.lobby.exceptions.CreateLobbyException
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.domain.lobby.services.LobbyService
import net.serverwars.sunsetPlugin.translations.sendTranslatedMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CommandLobbyCreate {

    fun run(ctx: CommandContext<CommandSourceStack>, size: Int, accessType: LobbyAccessType, gameType: GameServerType): Int {
        try {
            LobbyService.createLobby(size = size, accessType = accessType, gameType = gameType)
            ctx.source.sender.sendTranslatedMessage("command.lobby.create.success")

            if (accessType == LobbyAccessType.OPEN) {
                if (ctx.source.sender is Player) {
                    Bukkit.getServer().sendTranslatedMessage("command.lobby.create.success.open_announcement_by_player", ctx.source.sender.name)
                } else {
                    Audience.audience(Bukkit.getOnlinePlayers()).sendTranslatedMessage("command.lobby.create.success.open_announcement")
                }
            }

            if (ctx.source.sender is Player) {
                LobbyService.playerJoinLobby((ctx.source.sender as Player).uniqueId)
            }

            return Command.SINGLE_SUCCESS
        } catch (error: CreateLobbyException) {
            ctx.source.sender.sendTranslatedMessage(error.key, *error.args)
            return 0
        }
    }
}