package net.serverwars.sunsetPlugin.commands.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.serverwars.sunsetPlugin.domain.gameserver.services.GameServerService
import net.serverwars.sunsetPlugin.translations.Translations
import java.util.concurrent.CompletableFuture

object LobbyGameTypeArgumentType : CustomArgumentType.Converted<String, String> {

    private val NOT_ALLOWED = DynamicCommandExceptionType { gameType: Any ->
        MessageComponentSerializer.message().serialize(
            Translations.get("command.lobby.error.invalid_game_type", gameType)
        )
    }

    fun get(ctx: CommandContext<*>, name: String): String = ctx.getArgument(name, String::class.java)

    override fun convert(nativeType: String): String {
        return if (GameServerService.availableGameServerTypes?.types?.contains(nativeType) == true) nativeType
        else throw NOT_ALLOWED.create(nativeType)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (gameType in GameServerService.availableGameServerTypes?.types ?: emptyList()) {
            if (gameType.startsWith(builder.remainingLowerCase)) {
                builder.suggest(gameType)
            }
        }
        return builder.buildFuture()
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.word()
    }
}
