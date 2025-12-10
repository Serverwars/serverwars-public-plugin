package net.serverwars.sunsetPlugin.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.serverwars.sunsetPlugin.domain.lobby.models.Lobby
import net.serverwars.sunsetPlugin.translations.Translations
import java.util.concurrent.CompletableFuture

class LobbySizeArgumentType : CustomArgumentType<Int, Int> {

    companion object {
        private val NOT_ALLOWED = DynamicCommandExceptionType { size: Any ->
            MessageComponentSerializer.message().serialize(
                Translations.get("command.lobby.error.invalid_size", size)
            )
        }

        fun get(ctx: CommandContext<*>, name: String): Int =
            ctx.getArgument(name, Int::class.java)
    }

    override fun parse(reader: StringReader): Int {
        val start = reader.cursor
        val value = reader.readInt()
        if (value !in Lobby.ALLOWED_LOBBY_SIZES) {
            reader.cursor = start
            throw NOT_ALLOWED.create(value)
        }
        return value
    }

    override fun getNativeType(): ArgumentType<Int> {
        return IntegerArgumentType.integer()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        Lobby.ALLOWED_LOBBY_SIZES.forEach { builder.suggest(it) }
        return builder.buildFuture()
    }


}
