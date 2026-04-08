package net.serverwars.sunsetPlugin.commands.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.serverwars.sunsetPlugin.domain.gameservertype.models.gameservertype.GameServerType
import net.serverwars.sunsetPlugin.domain.gameservertype.services.GameServerTypeService
import net.serverwars.sunsetPlugin.translations.Translations
import java.util.concurrent.CompletableFuture

object LobbyGameTypeArgumentType : CustomArgumentType.Converted<GameServerType, String> {

    private val NOT_ALLOWED = DynamicCommandExceptionType { gameType: Any ->
        MessageComponentSerializer.message().serialize(
            Translations.get("command.lobby.error.invalid_game_type", gameType)
        )
    }

    fun get(ctx: CommandContext<*>, name: String): GameServerType =
        ctx.getArgument(name, GameServerType::class.java)

    override fun convert(nativeType: String): GameServerType =
        GameServerTypeService.getGameServerTypeFromName(nativeType) ?: throw NOT_ALLOWED.create(nativeType)

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (gameType in GameServerTypeService.availableGameServerTypes) {
            if (gameType.name.startsWith(builder.remainingLowerCase)) {
                builder.suggest(gameType.name)
            }
        }
        return builder.buildFuture()
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.word()
    }
}
