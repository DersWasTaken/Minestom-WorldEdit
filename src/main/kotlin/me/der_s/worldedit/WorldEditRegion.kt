package me.der_s.worldedit

import net.minestom.server.coordinate.Pos
import java.lang.Double
import java.text.NumberFormat
import kotlin.math.max
import kotlin.math.min

data class WorldEditRegion(val first: Pos, val second: Pos) {

    private val minX = min(first.x, second.x)
    private val maxX = Double.max(first.x, second.x)

    private val minY = max(min(first.y, second.y),-63.0)
    private val maxY = min(Double.max(first.y, second.y),319.0)

    private val minZ = min(first.z, second.z)
    private val maxZ = Double.max(first.z, second.z)
    
    fun getArea() : Int {
        return ((maxX - minX) * (maxY - minY) * (maxZ - minZ)).toInt()
    }

    fun getAreaFormatted() : String {
        return NumberFormat.getIntegerInstance().format(getArea())
    }

}
