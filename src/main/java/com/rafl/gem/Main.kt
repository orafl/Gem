package com.rafl.gem

import com.rafl.gem.core.*
import com.rafl.gem.gfx.getDefaultRenderer

class MySystem : ESystem() {
    var a = 0
    private var gameState: Asset<GameState>? = null
    private var write = Asset.overwrite("/entity/Game.json",
        AssetWritter.writeToJson(entity { it["id"] = "lol" }))

    override fun op(global: Entity?, e: Entity): Entity? {
        print(e)
        if (e.get<Unit>("_gemStub") != null) {
            a++
            if (a > 4*60 && write.finished() && gameState == null ) {
                gameState = Asset.load {
                    gameStateFromJson("/entity/Game.json")
                }
            }
            if (a > 6*60 && gameState != null) {
                val s = gameState?.get() ?: return e
                s.forEach(this::add)
                return null
            }
        }
        return e
    }
}

fun demo() {
    val initialState = stubState()//loadFromJson(json)
    val config = entity {
        it["name"] = "config"
    }
    val game = group(config, MySystem())
    gameLoop(getDefaultRenderer(), initialState, game)
}

fun main() {
    demo()
}