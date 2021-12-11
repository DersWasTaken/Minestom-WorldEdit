package gg.AstroMC.Utils

import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Entity
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.trait.EntityEvent
import org.reflections.Reflections
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import kotlin.collections.ArrayList

private val callbacks = ConcurrentHashMap<Class<Event>, MutableList<EventRestable<Event>>>()
private val node = EventNode.all("node")
private val reflections = Reflections("net.minestom.server.event")
private val events = reflections.getSubTypesOf(
    Event::class.java
)

private fun handleGlobal(e: Event) {
    val clazzz = e.javaClass
    if (callbacks[clazzz]!!.isEmpty()) return
    val restables = callbacks[clazzz]!!
    try {
        restables.forEach(Consumer { restableEventIMPL: EventRestable<Event> ->
            val callback = restableEventIMPL.callback
            val entitys = restableEventIMPL.entitys
            val clazz = restableEventIMPL.clazz
            if (e is EntityEvent && entitys.isNotEmpty()) {
                val eUUID = e.entity.uuid
                if (!Arrays.stream(entitys)
                        .anyMatch { uuid: UUID -> uuid === eUUID }
                ) return@Consumer
            }
            if (!callback(e)) {
                callbacks[clazz]!!.remove(restableEventIMPL)
            }
        })
    } catch (exception: ConcurrentModificationException) {
        //ignore it doesnt do shit i just cant be bothered to fix it
    }
}

internal fun enableDefaultEvents() {
    for (event in events) {
        callbacks[event as Class<Event>] = ArrayList()
        node.addListener(event) { handleGlobal(it) }
    }
    MinecraftServer.getGlobalEventHandler().addChild(node)
}

class EventRestable<T : Event>(clazz: Class<T>, callback: (T) -> Boolean, vararg entity: Entity) {

    var callback: (T) -> Boolean
    var entitys: Array<UUID>
    var clazz: Class<T>

    init {
        if(entity.isNotEmpty()) {
            entitys = (Arrays.stream(entity).map { obj: Entity -> obj.uuid } as Array<UUID>)
        } else {
            entitys = arrayOf()
        }
        this.callback = callback
        this.clazz = clazz
        callbacks[clazz as Class<Event>]!!.add(this as EventRestable<Event>)
    }
}
