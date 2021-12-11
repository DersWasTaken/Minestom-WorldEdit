package me.der_s.WorldEdit.Commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.der_s.Utils.Utils.toMini
import me.der_s.WorldEdit.WorldEditRegion
import me.der_s.WorldEdit.cuboid
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

class cuboid : WorldEditCommand("cuboid") {

    init {
        val width = ArgumentType.Integer("width");
        val height = ArgumentType.Integer("height");
        val length = ArgumentType.Integer("length");

        val block = ArgumentType.BlockState("block")

        setDefaultExecutor { sender, context ->
            sender.sendMessage("<red>Incorrect Usage: Use /cuboid <width> <height> <length> <block>".toMini())
        }

        width.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        height.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        length.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        block.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not a Block").toMini())
        }

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val w = context.get(width)
            val h = context.get(height)
            val l = context.get(length)

            val b = context.get(block)

            val pos = Pos(p.position)

            val worldEditRegion = WorldEditRegion(pos, Pos(pos.x + w, pos.y + h, pos.z + l))
            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green>Success! Pasted " + worldEditRegion.getAreaFormatted() + " blocks in " + cuboid(worldEditRegion, b, p.instance!!) + " ms").toMini())
            }
        }, width, height, length, block)
    }


}