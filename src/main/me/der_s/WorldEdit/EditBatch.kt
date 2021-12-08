package gg.AstroMC.WorldEdit

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.block.Block
import java.util.concurrent.ConcurrentHashMap

data class EditBatch(val blocks: ConcurrentHashMap<Pos, Block>, val chunk: Chunk) {

    override fun hashCode(): Int {
        return 0;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditBatch

        if (blocks != other.blocks) return false
        if (chunk != other.chunk) return false

        return true
    }

}
