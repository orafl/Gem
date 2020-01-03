package com.rafl.gem

import com.rafl.gem.core.*
import com.rafl.gem.gfx.getDefaultRenderer
import kotlinx.collections.immutable.plus

class Foo(val num: Int)

class MySystem : ESystem() {
    override fun op(global: Entity?, e: Entity): Entity? {
        println(e)
        if (e.get<Unit>("_gemStub") != null) {
            add(entity {it["name"] = c })
            c++
            //return null
        }
        val option = global?.get<Int>("option") ?: 0
        val a = option + c
        val cx = e.get<Foo>("foo1") ?: return e
        return e.put("foo1", Foo(cx.num + a))
    }
    private var c = 1
}

fun main() {
    /*val mut = mutableListOf(0, 1, 2, 5, 6)
    val i = mut.listIterator()
    val toAdd = listOf(3, 4)
    val stop = 5
    for(n in 0..toAdd.lastIndex) {
        while (i.hasNext()) {
            val p = i.next()
            if (p == stop) {
                i.previous()
                break
            }
        }
        i.add(toAdd[n])
        i.previous()
    }
    println(mut)*/
    demo()
}

fun demo() {
    val initialState = stubState() + entity {
        it["_gemHidden"] = true
        it["name"] = "config"
    }

    val game = group(MySystem()) {
        find { it.get<String>("name") == "config" }
    }
    gameLoop(getDefaultRenderer(), initialState, game)
}
