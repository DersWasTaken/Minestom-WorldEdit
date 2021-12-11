package me.der_s.WorldEdit.Commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.der_s.Utils.Utils.toMini
import me.der_s.WorldEdit.WorldEditRegion
import me.der_s.WorldEdit.cuboid
import me.der_s.WorldEdit.sphere
import net.minestom.server.command.builder.ArgumentCallback
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

class sphere : WorldEditCommand("sphere") {

    init {

        val radius = ArgumentType.Integer("radius")
        val block = ArgumentType.BlockState("block")

        setDefaultExecutor { sender, context ->
            sender.sendMessage("<red>Incorrect Usage: Use /sphere <radius> <block>".toMini())
        }

        radius.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        block.setCallback { sender, exception ->
            sender.sendMessage(("<red>ERROR: The value \"" + exception.input + "\" is not a Block").toMini())
        }

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val r = context.get(radius)
            val b = context.get(block)

            val pos = Pos(p.position)

            val blocks = 1.25 * (Math.PI * (r * r * r))

            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green>Success! Pasted " + blocks.toInt() + " blocks in " + sphere(pos, r, b, p.instance!!) + " ms").toMini())
            }
        }, radius, block)

    }

}