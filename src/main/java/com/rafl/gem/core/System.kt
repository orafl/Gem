package com.rafl.gem.core

import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList

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

fun group(vararg systems: ESystem, fglobal: GameState.() -> Entity?) = system { gs ->
    val global = fglobal(gs)
    val mut = gs.toMutableList()
    val i = mut.listIterator()
    while(i.hasNext()) {
        val ent = i.next()
        val stop = if (i.hasNext()) i.next() else null
        if (ent.get<Boolean>("_gemHidden") == true) continue
        var deleted = false
        val (newEnt, newState) = systems.fold(ent to gameStateWith()
        ) { acc, sys ->
            if (deleted) return@fold acc
            val r = sys(global, acc.first)
            val s = acc.second + sys.added
            deleted = r == null
            if (!deleted) (r!! to s) else (acc.first to s)
        }
        if(deleted) i.remove() else i.set(newEnt)

        if (newState.isEmpty()) continue
        for(n in 0..newState.lastIndex) {
            while (i.hasNext()) {
                val p = i.next()
                if (p == stop) {
                    i.previous()
                    break
                }
            }
            i.add(newState[n])
            i.previous()
        }
        //newState.forEach(i::add)
    }
    mut.toPersistentList()
}