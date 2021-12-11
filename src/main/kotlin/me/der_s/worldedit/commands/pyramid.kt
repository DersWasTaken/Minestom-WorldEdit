package me.der_s.worldedit.commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.der_s.utils.Utils.toMini
import me.der_s.worldedit.pyramid
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

class pyramid : WorldEditCommand("pyramid") {

    init {

        val size = ArgumentType.Integer("size")

        val block = ArgumentType.BlockState("block")

        setDefaultExecutor { sender, context ->
            sender.sendMessage("<red><bold>[WORLD-EDIT] Incorrect Usage: Use /pyramid <size> <block>".toMini())
        }

        size.setCallback { sender, exception ->
            sender.sendMessage(("<red><bold>[WORLD-EDIT] ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        block.setCallback { sender, exception ->
            sender.sendMessage(("<red><bold>[WORLD-EDIT] ERROR: The value \"" + exception.input + "\" is not a Block").toMini())
        }

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val s = context.get(size)
            val b = context.get(block)

            val pos = Pos(p.position)

            val blocks = (s * s * s) / 3

            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green><bold>[WORLD-EDIT] Success! Pasted " + blocks + " blocks in " + pyramid(s, pos, b, p.instance!!) + " ms").toMini())
            }
        }, size, block)

    }

}