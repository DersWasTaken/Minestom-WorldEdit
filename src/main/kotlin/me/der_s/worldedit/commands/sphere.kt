package me.der_s.worldedit.commands

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.der_s.utils.Utils.toMini
import me.der_s.worldedit.EditOptions
import me.der_s.worldedit.cylinder
import me.der_s.worldedit.sphere
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

class sphere : WorldEditCommand("sphere") {

    init {

        val radius = ArgumentType.Integer("radius")
        val block = ArgumentType.BlockState("block")

        val hollow = ArgumentType.Boolean("hollow");

        setDefaultExecutor { sender, context ->
            sender.sendMessage("<red><bold>[WORLD-EDIT] Incorrect Usage: Use /sphere <radius> <block>".toMini())
        }

        radius.setCallback { sender, exception ->
            sender.sendMessage(("<red><bold>[WORLD-EDIT] ERROR: The value \"" + exception.input + "\" is not an Integer").toMini())
        }

        block.setCallback { sender, exception ->
            sender.sendMessage(("<red><bold>[WORLD-EDIT] ERROR: The value \"" + exception.input + "\" is not a Block").toMini())
        }

        hollow.setCallback { sender, exception ->
            sender.sendMessage(("<red><bold>[WORLD-EDIT] ERROR: The value \"" + exception.input + "\" is not a Boolean").toMini())
        }

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val r = context.get(radius)
            val b = context.get(block)

            val isH = context.get(hollow)

            val pos = Pos(p.position)

            val blocks = 1.25 * (Math.PI * (r * r * r))

            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green><bold>[WORLD-EDIT] Success! Pasted " + blocks.toInt() + " blocks in " + sphere(pos, r, EditOptions(isH, b), p.instance!!).get() + " ms").toMini())
            }
        }, radius, block, hollow)

        addSyntax({ sender, context ->
            val p = (sender as Player)

            val r = context.get(radius)
            val b = context.get(block)

            val pos = Pos(p.position)

            val blocks = 1.25 * (Math.PI * (r * r * r))

            CoroutineScope(Dispatchers.IO).launch {
                sender.sendMessage(("<green><bold>[WORLD-EDIT] Success! Pasted " + blocks.toInt() + " blocks in " + sphere(pos, r, EditOptions(false, b), p.instance!!).get() + " ms").toMini())
            }
        }, radius, block)

    }

}