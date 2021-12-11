package me.der_s.WorldEdit.Commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.der_s.Utils.Utils.toMini
import me.der_s.WorldEdit.WorldEditRegion
import me.der_s.WorldEdit.cuboid
import me.der_s.WorldEdit.pyramid
import me.der_s.WorldEdit.sphere
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

class pyramid : WorldEditCommand("pyramid") {

    init {

        val size = ArgumentType.Integer("size")

        val block = ArgumentType.BlockState("block")

        setDefaultExecutor { sender, context ->
            sender.sendMessage("<red>Incorrect Usage: Use /pyramid <size> <block>".toMini())
        }

        size.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        block.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not a Block").toMini())
        }

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val s = context.get(size)
            val b = context.get(block)

            val pos = Pos(p.position)

            val blocks = (s * s * s) / 3

            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green>Success! Pasted " + blocks + " blocks in " + pyramid(s, pos, b, p.instance!!) + " ms").toMini())
            }
        }, size, block)

    }

}