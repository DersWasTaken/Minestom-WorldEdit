package gg.AstroMC.Commands

import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.ArgumentCallback
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandExecutor

class StopCommand : Command("stop") {

    init{
        defaultExecutor = CommandExecutor { _: CommandSender?, _: CommandContext ->
            run {
                MinecraftServer.stopCleanly();
            }
        }
    }

}