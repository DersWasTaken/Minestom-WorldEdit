package gg.AstroMC.WorldEdit

import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import java.util.concurrent.ConcurrentHashMap

internal object WorldEdit {

    suspend fun setBlocks(editBatch: EditBatch) {
        val chunk = editBatch.chunk
        for (block in editBatch.blocks) {
            val key = block.key;
            val block = block.value

            chunk.setBlock(key,block)
            chunk.sendChunk()
        }
    }

}

internal suspend fun cuboid(width: Int, height: Int, length: Int,
                            x: Int, y: Int, z: Int, instance: Instance) {
    if(y + height > 319) return

    for(x1 in x until (width + x)) {
        for(z1 in z until (length + z)) {
            val chunk = instance.getChunkAt(((x1).toDouble()), ((z1).toDouble()))
            val map = ConcurrentHashMap<Pos, Block>()

            for (y1 in y until (height + y)) {
                map[Pos(x1.toDouble(),y1.toDouble(),z1.toDouble())] = Block.STONE
            }
            WorldEdit.setBlocks(EditBatch(map, chunk!!))
        }
    }
}