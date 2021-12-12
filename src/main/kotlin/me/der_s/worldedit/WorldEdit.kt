package me.der_s.worldedit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import me.der_s.worldedit.commands.selectedregion
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import java.lang.Math.pow
import java.util.concurrent.CompletableFuture
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

internal object WorldEdit {

    val playerRegions = mutableMapOf<Player, selectedregion>()

}

fun getPointsBetween(first: Pos, second: Pos): Sequence<Pos> {
    return sequence {
        val minMax = minMax(first, second)

        val minArray = minMax.first
        val maxArray = minMax.second

        val xSize = abs((maxArray[0] - minArray[0]))
        val ySize = abs((maxArray[1] - minArray[1]))
        val zSize = abs((maxArray[2] - minArray[2]))

        var x = minArray[0]
        var y = minArray[1]
        var z = minArray[2]

        val area = if(ySize != 0) xSize * zSize * ySize else { xSize * zSize }

        repeat(area) {
            if(x + 1 > maxArray[0]) {
                x = minArray[0]
                z++
            }
            if(z + 1 > maxArray[2]) {
                z = minArray[2]
                y++
            }
            yield(Pos(x.toDouble(),y.toDouble(),z.toDouble()))
            x++
        }

    }
}

fun minMax(first: Pos, second: Pos): Pair<Array<Int>, Array<Int>> {
    return Pair(
        arrayOf(min(first.blockX(), second.blockX()), min(first.blockY(), second.blockY()), min(first.blockZ(), second.blockZ())),
        arrayOf(max(first.blockX(), second.blockX()), max(first.blockY(), second.blockY()), max(first.blockZ(), second.blockZ()))
    )
}

internal suspend fun sphere(pos: Pos, radius: Int,  editOptions: EditOptions, instance: Instance): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()

    val start = System.currentTimeMillis()

    val map = hashMapOf<Chunk, AbsoluteBlockBatch>()

    val iter = getPointsBetween(pos, pos.apply { x, y, z, yaw, pitch ->
        return@apply Pos(x + radius, y + radius, z + radius)
    }).iterator()

    val center = pos.apply { x, y, z, yaw, pitch ->
        return@apply Pos(x + (radius / 2), y + (radius / 2), z + (radius / 2))
    }

    while (iter.hasNext()) {
        val b = iter.next()

        val x = b.blockX()
        val y = b.blockY()
        val z = b.blockZ()

        val dist =
            (((x - center.blockX()) * (x - center.blockX()) + (y - center.blockY()) * (y - center.blockY()) + (z - center.blockZ()) * (z - center.blockZ())).toDouble()).pow(0.5)

        if (dist +- 0.5 <= (radius) / 2) {
            val chunk = instance.getChunkAt(x.toDouble(), z.toDouble()) ?: continue
            val chunkBatch = map[chunk] ?: AbsoluteBlockBatch()

            chunkBatch.setBlock(x, y, z, editOptions.block)

            map[chunk] = chunkBatch
        }
    }

    var end: Long = 0

    for ((_,value) in map) {
        value.apply(instance) {
            end = System.currentTimeMillis()
            future.complete((end - start))
        }
    }

    if(editOptions.hollow) {
        sphere(pos.apply { x, y, z, yaw, pitch ->
            Pos(x + 1, y + 1, z + 1)
        }, radius - 2, EditOptions(false, Block.AIR), instance)
    }

    return future;

}

internal suspend fun cylinder(pos: Pos, height: Int, radius: Int, editOptions: EditOptions, instance: Instance): CompletableFuture<Long> {
    val future = CompletableFuture<Long>()

    val start = System.currentTimeMillis()

    val map = hashMapOf<Chunk, AbsoluteBlockBatch>()

    val iter = getPointsBetween(pos, pos.apply { x, y, z, yaw, pitch ->
        return@apply Pos(x + radius, y + height, z + radius)
    }).iterator()

    val center = pos.apply { x, y, z, yaw, pitch ->
        return@apply Pos(x + (radius / 2), y + (height / 2), z + (radius / 2))
    }

    while (iter.hasNext()) {
        val b = iter.next()

        val x = b.blockX()
        val y = b.blockY()
        val z = b.blockZ()

        val dist =
            (((x - center.blockX()) * (x - center.blockX()) + (z - center.blockZ()) * (z - center.blockZ())).toDouble()).pow(0.5)

        if (dist +- 0.5 <= (radius) / 2) {
            val chunk = instance.getChunkAt(x.toDouble(), z.toDouble()) ?: continue
            val chunkBatch = map[chunk] ?: AbsoluteBlockBatch()

            chunkBatch.setBlock(x, y, z, editOptions.block)

            map[chunk] = chunkBatch
        }
    }

    var end: Long = 0

    for ((_,value) in map) {
        value.apply(instance) {
            end = System.currentTimeMillis()
            future.complete((end - start))
        }
    }

    if(editOptions.hollow) {
        cylinder(pos.apply { x, y, z, yaw, pitch ->
            Pos(x + 1, y + 1, z + 1)
        }, height - 2, radius - 2, EditOptions(false, Block.AIR), instance)
    }

    return future;

}

internal suspend fun pyramid(size: Int, pos: Pos,  editOptions: EditOptions, instance: Instance) : CompletableFuture<Long>  {
    val future = CompletableFuture<Long>()

    val start = System.currentTimeMillis()

    var offset = size

    var t: Long = 0

    for(y in pos.y.toInt() until (pos.y + size).toInt()) {
        val leftPos = Pos(pos.x  - offset + size, y.toDouble(), pos.z - offset + size)
        val rightPos = Pos(pos.x + offset + size, y.toDouble(), pos.z + offset + size)
        val worldEditRegion = WorldEditRegion(leftPos, rightPos);
        t += cuboid(worldEditRegion, editOptions, instance).get()
        offset--
    }

    val end = System.currentTimeMillis()

    future.complete((end - start) + t)

    return future;
}

internal suspend fun cuboid(worldEditRegion: WorldEditRegion, editOptions: EditOptions, instance: Instance): CompletableFuture<Long> {

    val future = CompletableFuture<Long>()

    val start = System.currentTimeMillis()

    val blocks = getPointsBetween(worldEditRegion.first, worldEditRegion.second)

    val map = hashMapOf<Chunk, AbsoluteBlockBatch>()

    val iterator = blocks.iterator()

    while(iterator.hasNext()) {
        val pos = iterator.next()

        val chunk = instance.getChunkAt(pos.x, pos.z) ?: continue
        val chunkBatch = map[chunk] ?: AbsoluteBlockBatch()

        chunkBatch.setBlock(pos.blockX(), pos.blockY(), pos.blockZ(), editOptions.block)

        map[chunk] = chunkBatch
    }

    var end: Long = 0

    for ((chunk,value) in map) {
        value.apply(instance) {
            end = System.currentTimeMillis()
            future.complete((end - start))
        }
    }

    if(editOptions.hollow ) {
        val pos1 = worldEditRegion.first.apply { x, y, z, yaw, pitch ->
            Pos(x + 1, y + 1, z + 1)
        }
        val pos2 =  worldEditRegion.second.apply { x, y, z, yaw, pitch ->
            Pos(x - 1, y - 1, z - 1)
        }
        cuboid(WorldEditRegion(pos1, pos2), EditOptions(false, Block.AIR), instance)
    }

    return future


}