package me.der_s.Utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor

internal object Utils {

    internal fun String.toMini() : Component {
        return MiniMessage.builder().markdownFlavor(DiscordFlavor.get()).build().parse(toString())
    }

}