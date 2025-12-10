package net.serverwars.sunsetPlugin.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.serverwars.sunsetPlugin.commands.arguments.LobbyAccessTypeArgumentType
import net.serverwars.sunsetPlugin.commands.arguments.LobbyGameTypeArgumentType
import net.serverwars.sunsetPlugin.commands.arguments.LobbySizeArgumentType
import net.serverwars.sunsetPlugin.commands.config.CommandConfigReload
import net.serverwars.sunsetPlugin.commands.lobby.*
import net.serverwars.sunsetPlugin.commands.queue.CommandQueueEnter
import net.serverwars.sunsetPlugin.commands.queue.CommandQueueLeave
import org.bukkit.command.CommandSender

object CommandServerwars {

    private fun hasAnyChildPermission(sender: CommandSender, vararg perms: String): Boolean {
        return perms.any { sender.hasPermission(it) }
    }

    val command: LiteralCommandNode<CommandSourceStack> = Commands.literal("serverwars")
        // CONFIG COMMAND
        .then(Commands.literal("config")
            .requires { hasAnyChildPermission(it.sender, "serverwars.commands.config.reload") }
            .then(Commands.literal("reload")
                .requires { it.sender.hasPermission("serverwars.commands.config.reload") }
                .executes(CommandConfigReload::run)
            )
        )

        // LOBBY COMMAND
        .then(Commands.literal("lobby")
            .requires {
                hasAnyChildPermission(
                    it.sender,
                    "serverwars.commands.lobby.create",
                    "serverwars.commands.lobby.set.size",
                    "serverwars.commands.lobby.set.access",
                    "serverwars.commands.lobby.invite",
                    "serverwars.commands.lobby.uninvite",
                    "serverwars.commands.lobby.join",
                    "serverwars.commands.lobby.leave",
                    "serverwars.commands.lobby.kick",
                    "serverwars.commands.lobby.delete"
                )
            }
            .then(Commands.literal("create")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.create") }
                .then(Commands.argument("size", LobbySizeArgumentType())
                    .then(Commands.argument("access type", LobbyAccessTypeArgumentType())
                        .then(Commands.argument("game type", LobbyGameTypeArgumentType())
                            .executes { CommandLobbyCreate.run(
                                ctx = it,
                                size = LobbySizeArgumentType.get(it, "size"),
                                accessType = LobbyAccessTypeArgumentType.get(it, "access type"),
                                gameType = LobbyGameTypeArgumentType.get(it, "game type")
                            ) }
                        )
                    )
                )
            )
            .then(Commands.literal("set")
                .requires {
                    hasAnyChildPermission(
                        it.sender,
                        "serverwars.commands.lobby.set",
                        "serverwars.commands.lobby.set.size",
                        "serverwars.commands.lobby.set.access",
                        "serverwars.commands.lobby.set.game",
                    )
                }
                .then(Commands.literal("size")
                    .requires { it.sender.hasPermission("serverwars.commands.lobby.set.size") }
                    .then(Commands.argument("size", LobbySizeArgumentType())
                        .executes { CommandLobbySetSize.run(
                            ctx = it,
                            size = LobbySizeArgumentType.get(it, "size")
                        ) }
                    )
                )
                .then(Commands.literal("access")
                    .requires { it.sender.hasPermission("serverwars.commands.lobby.set.access") }
                    .then(Commands.argument("access type", LobbyAccessTypeArgumentType())
                        .executes { CommandLobbySetAccessType.run(
                            ctx = it,
                            accessType = LobbyAccessTypeArgumentType.get(it, "access type")
                        ) }
                    )
                )
                .then(Commands.literal("game")
                    .requires { it.sender.hasPermission("serverwars.commands.lobby.set.game") }
                    .then(Commands.argument("game type", LobbyGameTypeArgumentType())
                        .executes { CommandLobbySetGameType.run(
                            ctx = it,
                            gameType = LobbyGameTypeArgumentType.get(it, "game type")
                        ) }
                    )
                )
            )
            .then(Commands.literal("invite")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.invite") }
                .then(Commands.argument("invitee", ArgumentTypes.player())
                    .executes {
                        val targetResolver = it.getArgument("invitee", PlayerSelectorArgumentResolver::class.java)
                        val target = targetResolver.resolve(it.source).first()
                        CommandLobbyInvite.run(ctx = it, invitee = target)
                    }
                )
                .then(Commands.literal("accept")
                    .executes { CommandLobbyInviteAccept.self(it) })
                .then(Commands.literal("decline")
                    .executes { CommandLobbyInviteDecline.self(it) })
            )
            .then(Commands.literal("uninvite")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.uninvite") }
                .then(Commands.argument("uninvitee", ArgumentTypes.player())
                    .executes {
                        val targetResolver = it.getArgument("uninvitee", PlayerSelectorArgumentResolver::class.java)
                        val target = targetResolver.resolve(it.source).first()
                        CommandLobbyUninvite.run(ctx = it, uninvitee = target)
                    }
                )
            )
            .then(Commands.literal("join")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.join") }
                .executes(CommandLobbyJoin::self)
                .then(Commands.argument("joiner", ArgumentTypes.player())
                    .executes {
                        val targetResolver = it.getArgument("joiner", PlayerSelectorArgumentResolver::class.java)
                        val target = targetResolver.resolve(it.source).first()
                        CommandLobbyJoin.run(joiner = target)
                    }
                )
            )
            .then(Commands.literal("leave")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.leave") }
                .executes(CommandLobbyLeave::self)
            )
            .then(Commands.literal("kick")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.kick") }
                .then(Commands.argument("kicked", ArgumentTypes.player())
                    .executes {
                        val targetResolver = it.getArgument("kicked", PlayerSelectorArgumentResolver::class.java)
                        val target = targetResolver.resolve(it.source).first()
                        CommandLobbyKick.run(ctx = it, kicked = target)
                    }
                )
            )
            .then(Commands.literal("delete")
                .requires { it.sender.hasPermission("serverwars.commands.lobby.delete") }
                .executes(CommandLobbyDelete::run)
                .then(Commands.literal("confirm").executes(CommandLobbyDeleteConfirm::run))
            )
        )

        // QUEUE COMMAND
        .then(Commands.literal("queue")
            .requires {
                hasAnyChildPermission(
                    it.sender,
                    "serverwars.commands.queue.enter",
                    "serverwars.commands.queue.leave"
                )
            }
            .then(Commands.literal("enter")
                .requires { it.sender.hasPermission("serverwars.commands.queue.enter") }
                .executes(CommandQueueEnter::run)
            )
            .then(Commands.literal("leave")
                .requires { it.sender.hasPermission("serverwars.commands.queue.leave") }
                .executes(CommandQueueLeave::run)
            )
        )

        .build()

}