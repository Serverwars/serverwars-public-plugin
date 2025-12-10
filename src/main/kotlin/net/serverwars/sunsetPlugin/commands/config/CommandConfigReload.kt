package net.serverwars.sunsetPlugin.commands.config

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.serverwars.sunsetPlugin.Main
import net.serverwars.sunsetPlugin.translations.TranslationManager

object CommandConfigReload {

    fun run(ctx: CommandContext<CommandSourceStack>): Int {
        Main.inst.reloadConfig()
        TranslationManager.loadTranslations()
        ctx.source.sender.sendMessage(Component.text("Config reloaded.", NamedTextColor.GRAY))
        return Command.SINGLE_SUCCESS
    }
}