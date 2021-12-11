package me.der_s.WorldEdit

import net.minestom.server.coordinate.Pos
import java.lang.Double
import java.text.NumberFormat
import kotlin.math.max
import kotlin.math.min

data class WorldEditRegion(val first: Pos, val second: Pos) {

    val minX = min(first.x, second.x)
    val maxX = Double.max(first.x, second.x)

    val minY = max(min(first.y, second.y),-63.0)
    val maxY = min(Double.max(first.y, second.y),319.0)

    val minZ = min(first.z, second.z)
    val maxZ = Double.max(first.z, second.z)
    
    fun getArea() : Int {
        return ((maxX - minX) * (maxY - minY) * (maxZ - minZ)).toInt()
    }

    fun getAreaFormatted() : String {
        return NumberFormat.getIntegerInstance().format((maxX - minX) * (maxY - minY) * (maxZ - minZ))
    }

}
