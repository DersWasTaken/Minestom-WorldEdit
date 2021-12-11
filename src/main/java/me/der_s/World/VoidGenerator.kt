package gg.AstroMC.World

import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block

class VoidGenerator : ChunkGenerator {
    override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
        for(x in 0 until 16) {
            for(z in 0 until 16) {
                batch.setBlock(x,50,z, Block.MOSS_BLOCK)
            }
        }
    }

    override fun getPopulators(): MutableList<ChunkPopulator>? {
        return null
    }
}