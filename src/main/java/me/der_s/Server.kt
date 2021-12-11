package me.der_s

import gg.AstroMC.Utils.EventRestable
import gg.AstroMC.Utils.enableDefaultEvents
import gg.AstroMC.World.VoidGenerator
import kotlinx.coroutines.DelicateCoroutinesApi
import me.der_s.Commands.StopCommand
import me.der_s.WorldEdit.Commands.cuboid
import me.der_s.WorldEdit.Commands.pyramid
import me.der_s.WorldEdit.Commands.sphere
import net.minestom.server.MinecraftServer
import net.minestom.server.UpdateManager
import net.minestom.server.advancements.AdvancementManager
import net.minestom.server.adventure.bossbar.BossBarManager
import net.minestom.server.command.CommandManager
import net.minestom.server.coordinate.Pos
import net.minestom.server.data.DataManager
import net.minestom.server.entity.GameMode
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.exception.ExceptionManager
import net.minestom.server.extensions.ExtensionManager
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.optifine.OptifineSupport
import net.minestom.server.gamedata.tags.TagManager
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.block.BlockManager
import net.minestom.server.listener.manager.PacketListenerManager
import net.minestom.server.monitoring.BenchmarkManager
import net.minestom.server.network.ConnectionManager
import net.minestom.server.network.socket.Server
import net.minestom.server.recipe.RecipeManager
import net.minestom.server.scoreboard.TeamManager
import net.minestom.server.storage.StorageManager
import net.minestom.server.timer.SchedulerManager
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import net.minestom.server.world.DimensionTypeManager
import net.minestom.server.world.biomes.BiomeManager
import org.slf4j.Logger

internal object Server {

    /**
     * OPTIONS:
     * BRAND-NAME || NAME OF SERVER
     * DIFFICULTY || PEACEFUL/EASY/NORMAL/HARD
     * TERMINAL-ENABLED || TRUE/FALSE
     * COMPRESSION-THRESHOLD || INTEGER
     * MAX-PACKET-SIZE || INTEGER
     * RATE-LIMIT || INTEGER
     * CHUNK-VIEW-DISTANCE || 1-32
     * ENTITY-VIEW-DISTANCE || 1-32
     */
    const val BRAND_NAME = "Minestom-Worldedit"

    lateinit var LOGGER: Logger
    lateinit var MINECRAFT_SERVER: MinecraftServer
    lateinit var SERVER: Server
    lateinit var GLOBAL_EVENT_HANDLER: GlobalEventHandler
    lateinit var INSTANCE_MANAGER: InstanceManager
    lateinit var COMMAND_MANAGER: CommandManager
    lateinit var CONNECTION_MANAGER: ConnectionManager
    lateinit var TEAM_MANAGER: TeamManager
    lateinit var SCHEDULER_MANAGER: SchedulerManager
    lateinit var ADVANCEMENT_MANAGER: AdvancementManager
    lateinit var BENCHMARK_MANAGER: BenchmarkManager
    lateinit var BIOME_MANAGER: BiomeManager
    lateinit var BLOCK_MANAGER: BlockManager
    lateinit var BOSS_BAR_MANAGER: BossBarManager
    lateinit var DIMENSION_TYPE_MANAGER: DimensionTypeManager
    lateinit var EXCEPTION_MANAGER: ExceptionManager
    lateinit var EXTENSION_MANAGER: ExtensionManager
    lateinit var PACKET_LISTENER_MANAGER: PacketListenerManager
    lateinit var RECIPE_MANAGER: RecipeManager
    lateinit var STORAGE_MANAGER: StorageManager
    lateinit var TAG_MANAGER: TagManager
    lateinit var UPDATE_MANAGER: UpdateManager
    lateinit var DATA_MANAGER: DataManager

    lateinit var WORLD: Instance

    private fun init() {
        /*
        BUILD LOGGER
        */
        LOGGER = MinecraftServer.LOGGER

        /*
        BUILD SERVER
         */
        MINECRAFT_SERVER = MinecraftServer.init()
        SERVER = MinecraftServer.getServer()

        /*
        BUILD STATIC MANAGER'S
         */
        GLOBAL_EVENT_HANDLER = MinecraftServer.getGlobalEventHandler()
        INSTANCE_MANAGER = MinecraftServer.getInstanceManager()
        COMMAND_MANAGER = MinecraftServer.getCommandManager()
        CONNECTION_MANAGER = MinecraftServer.getConnectionManager()
        TEAM_MANAGER = MinecraftServer.getTeamManager()
        SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager()
        ADVANCEMENT_MANAGER = MinecraftServer.getAdvancementManager()
        BENCHMARK_MANAGER = MinecraftServer.getBenchmarkManager()
        BIOME_MANAGER = MinecraftServer.getBiomeManager()
        BLOCK_MANAGER = MinecraftServer.getBlockManager()
        BOSS_BAR_MANAGER = MinecraftServer.getBossBarManager()
        DIMENSION_TYPE_MANAGER = MinecraftServer.getDimensionTypeManager()
        EXCEPTION_MANAGER = MinecraftServer.getExceptionManager()
        EXTENSION_MANAGER = MinecraftServer.getExtensionManager()
        PACKET_LISTENER_MANAGER = MinecraftServer.getPacketListenerManager()
        RECIPE_MANAGER = MinecraftServer.getRecipeManager()
        STORAGE_MANAGER = MinecraftServer.getStorageManager()
        TAG_MANAGER = MinecraftServer.getTagManager()
        UPDATE_MANAGER = MinecraftServer.getUpdateManager()
        DATA_MANAGER = MinecraftServer.getDataManager()

        /*
        SET OPTIONS
         */
        MinecraftServer.setBrandName(BRAND_NAME)

        /*
        ENABLE OPTIFINE SUPPORT
         */
        OptifineSupport.enable()

        /*
        ENABLE AUTH
         */
        MojangAuth.init()

        /*
        ENABLE RESTABLES
         */
        enableDefaultEvents()

        /*
        CREATE WORLD
         */

        val OVERWORLD = DimensionType.builder(NamespaceID.from("minecraft:overworld2"))
            .ultrawarm(false)
            .natural(true)
            .piglinSafe(false)
            .respawnAnchorSafe(false)
            .bedSafe(true)
            .raidCapable(true)
            .skylightEnabled(true)
            .ceilingEnabled(false)
            .fixedTime(null)
            .ambientLight(0.0f)
            .height(320)
            .logicalHeight(320)
            .infiniburn(NamespaceID.from("minecraft:infiniburn_overworld"))
            .minY(-64)
            .build()

        DIMENSION_TYPE_MANAGER.addDimension(OVERWORLD)

        WORLD = INSTANCE_MANAGER.createInstanceContainer(OVERWORLD)
        WORLD.chunkGenerator = VoidGenerator()

        /*
        BUILD SHUTDOWN TASK
         */
        SCHEDULER_MANAGER.buildShutdownTask { }.build().schedule()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @JvmStatic
    fun main(args: Array<String>) {
        /*
        INITIALIZE SERVER
         */
        init()

        /*
        START SERVER
         */
        MINECRAFT_SERVER.start("0.0.0.0", 25565)

        /*
        REGISTER COMMANDS
         */
        COMMAND_MANAGER.register(StopCommand())
        COMMAND_MANAGER.register(cuboid())
        COMMAND_MANAGER.register(sphere())
        COMMAND_MANAGER.register(pyramid())

        /*
        ENABLE DEFAULT RESTABLES
         */
        EventRestable(PlayerLoginEvent::class.java, { playerLoginEvent: PlayerLoginEvent ->
            val p = playerLoginEvent.player
            playerLoginEvent.setSpawningInstance(WORLD)
            p.setGameMode(GameMode.SPECTATOR)
            p.isAllowFlying = true
            true
        })

        EventRestable(PlayerSpawnEvent::class.java, {playerSpawnEvent ->
            playerSpawnEvent.player.teleport( Pos(0.0,50.0,0.0))
            true })
    }

}