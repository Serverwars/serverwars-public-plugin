package net.serverwars.sunsetPlugin.commands.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.serverwars.sunsetPlugin.domain.lobby.models.LobbyAccessType
import net.serverwars.sunsetPlugin.translations.Translations
import java.util.concurrent.CompletableFuture

class LobbyAccessTypeArgumentType : CustomArgumentType.Converted<LobbyAccessType, String> {

    companion object {
        private val NOT_ALLOWED = DynamicCommandExceptionType { accessType: Any ->
            MessageComponentSerializer.message().serialize(
                Translations.get("command.lobby.error.invalid_access_type", accessType)
            )
        }

        fun get(ctx: CommandContext<*>, name: String): LobbyAccessType =
            ctx.getArgument(name, LobbyAccessType::class.java)
    }

    override fun convert(nativeType: String): LobbyAccessType {
        return LobbyAccessType.fromValue(nativeType)
            ?: throw NOT_ALLOWED.create(nativeType)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        for (accessType in LobbyAccessType.entries) {
            if (accessType.value.startsWith(builder.remainingLowerCase)) {
                builder.suggest(accessType.value)
            }
        }
        return builder.buildFuture()
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.word()
    }
}
