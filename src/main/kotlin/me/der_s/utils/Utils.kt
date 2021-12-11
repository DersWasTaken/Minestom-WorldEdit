package me.der_s.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor

internal object Utils {

    internal fun String.toMini() : Component {
        return MiniMessage.builder().markdownFlavor(DiscordFlavor.get()).build().parse(toString())
    }

}