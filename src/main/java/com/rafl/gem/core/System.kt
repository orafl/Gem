package com.rafl.gem.core

import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import java.util.*
import kotlin.collections.LinkedHashSet

typealias System = (GameState) -> GameState

inline fun system(crossinline s: System): System = { state -> s(state) }

fun chain(vararg systems: System) = system { gs ->
    systems.fold(gs) { acc, sys -> sys(acc) }
}

abstract class ESystem {
    internal var added = gameStateWith()
    fun add(e: Entity) { added += e }

    abstract fun op(global:Entity?, e:Entity): Entity?
    operator fun invoke(global:Entity?, e:Entity): Entity? {
        added = gameStateWith()
        return op(global, e)
    }
}

fun group(global: Entity?, vararg systems: ESystem) = system { gs ->
    val mut: Queue<Entity> = LinkedList(gs.toMutableList())
    val repeats = LinkedHashSet<Entity>()
    while (mut.size > 0) {
        val ent = mut.remove()
        if (ent in repeats) continue

        var deleted = false
        val (newEnt, newState) = systems.fold(ent to gameStateWith()
        ) { acc, sys ->
            if (deleted) return@fold acc
            val r = sys(global, acc.first)
            val s = acc.second + sys.added
            deleted = r == null
            if (!deleted) (r!! to s) else (acc.first to s)
        }

        if (!deleted) {
            repeats.add(newEnt); mut.add(newEnt)
        }
        newState.forEach { mut.add(it) }
    }

    repeats.toPersistentList()
}