package com.rafl.gem.core

import com.rafl.gem.gfx.Renderer
import com.rafl.gem.gfx.getDefaultRenderer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

typealias GameState = PersistentList<Entity>
fun emptyState(): GameState = persistentListOf()

fun gameLoop(renderer: Renderer, config: Config, system: System) = runBlocking {
    val shared = Channel<GameState>(Channel.CONFLATED)

    renderer.load()
    launch(Dispatchers.Default) {
        updateLoop(config, system, shared)
    }
    renderLoop(renderer, shared)
}

private suspend fun CoroutineScope.updateLoop(config: Config, system: System, channel: Channel<GameState>) {
    var gameState = emptyState()
    var ticks = 0
    var timer = java.lang.System.currentTimeMillis()
    val dt = (1e9/60).toLong()
    var elapsed = 0L
    var now: Long
    var before = java.lang.System.nanoTime()
    while(isActive) {
        now = java.lang.System.nanoTime()
        elapsed += now - before
        before = now

        if(elapsed >= dt) {
            elapsed = 0
            ticks++
            gameState = system(config, gameState)
            channel.send(gameState)
        }

        if (java.lang.System.currentTimeMillis() - timer >= 1000) {
            println(ticks)
            timer += 1000
            ticks = 0
        }
    }
}

private suspend fun renderLoop(renderer: Renderer, channel: ReceiveChannel<GameState>) {
    while(true) {
        //var oldState : GameState? = null
        for (newState in channel) {
            //if (oldState == newState) continue
            renderer.onRender(newState)
            //oldState = newState
        }
    }
}