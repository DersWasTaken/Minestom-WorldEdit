package me.der_s.WorldEdit

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.Instance
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import kotlin.math.max
import kotlin.math.min

internal object WorldEdit {

}

internal suspend fun sphere(pos: Pos, radius: Int, block: Block, instance: Instance): Long {
    val start = System.currentTimeMillis()

    val sqrRadius = radius * radius

    val X = pos.x.toInt()
    val Y = pos.y.toInt()
    val Z = pos.z.toInt()

    val map = hashMapOf<Chunk, AbsoluteBlockBatch>()

    for(x in (X - radius) until (X + radius)) {
        for(y in (Y - radius) until (max(min(Y + radius, 319), -63))) {
            for(z in (Z - radius) until (Z + radius)) {
                if ((X - x) * (X - x) + (Y - y) * (Y - y) + (Z - z) * (Z - z) <= sqrRadius) {
                    val chunk = instance.getChunkAt(x.toDouble(), z.toDouble()) ?: continue
                    val chunkBatch = map[chunk] ?: AbsoluteBlockBatch()

                    chunkBatch.setBlock(x,y,z, block)

                    map[chunk] = chunkBatch
                }
            }
        }
    }

    for ((_,value) in map) {
        value.apply(instance) {}
    }

    val end = System.currentTimeMillis()

    return (end - start)

}

internal suspend fun pyramid(size: Int, pos: Pos, block: Block, instance: Instance) : Long {
    val start = System.currentTimeMillis()

    var offset = size

    for(y in pos.y.toInt() until (pos.y + size).toInt()) {
        val leftPos = Pos(pos.x  - offset + size, y.toDouble(), pos.z - offset + size)
        val rightPos = Pos(pos.x + offset + size, y.toDouble(), pos.z + offset + size)

        val worldEditRegion = WorldEditRegion(leftPos, rightPos)
        cuboid(worldEditRegion, block, instance)
        offset--
    }

    val end = System.currentTimeMillis()

    return (end - start)
}

internal suspend fun cuboid(worldEditRegion: WorldEditRegion, block: Block, instance: Instance): Long {

    val start = System.currentTimeMillis()

    val maxX = worldEditRegion.maxX
    val minX = worldEditRegion.minX

    val maxY = worldEditRegion.maxY
    val minY = worldEditRegion.minY

    val maxZ = worldEditRegion.maxZ
    val minZ = worldEditRegion.minZ

    val map = hashMapOf<Chunk, AbsoluteBlockBatch>()

     for(x in minX.toInt() until maxX.toInt()) for(y in minY.toInt() until maxY.toInt() + 1) for(z in minZ.toInt() until maxZ.toInt()) {
        val chunk = instance.getChunkAt(minX, minZ) ?: continue
        val chunkBatch = map[chunk] ?: AbsoluteBlockBatch()

        chunkBatch.setBlock(x,y,z,block)

        map[chunk] = chunkBatch
    }

    for ((chunk,value) in map) {
        instance.loadChunk(chunk.chunkX, chunk.chunkZ)
        value.apply(instance) {}
    }

    val end = System.currentTimeMillis()

    return (end - start)


}