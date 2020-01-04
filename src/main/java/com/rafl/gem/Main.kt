package com.rafl.gem

import com.rafl.gem.core.*
import com.rafl.gem.gfx.getDefaultRenderer
import kotlinx.collections.immutable.plus

class MySystem : ESystem() {
    private var c = 1
    override fun op(global: Entity?, e: Entity): Entity? {
        println(e)
        if (e.get<Unit>("_gemStub") != null) {
            add(entity {it["name"] = c })
            c++
            return null
        }

        if (e.get<Int>("name") == 1) {
            add(entity {it["name"] = c })
            c++
        }
        return e
    }
}

fun demo() {
    val initialState = stubState()
    val config = entity {
        it["name"] = "config"
    }
    val game = group(config, MySystem())
    gameLoop(getDefaultRenderer(), initialState, game)
}

fun main() {
    demo()
}