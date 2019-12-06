package com.rafl.gem.core

import com.rafl.gem.gfx.Renderer
import com.rafl.gem.gfx.getDefaultRenderer
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

typealias GameState = PersistentList<Entity>

fun gameLoop(system: System) = runBlocking {
    val shared = Channel<GameState>(Channel.CONFLATED)
    val renderer = getDefaultRenderer()

    renderer.load()
    launch(Dispatchers.Default) {
        updateLoop(system, shared)
    }
    renderLoop(renderer, shared)
}

private suspend fun CoroutineScope.updateLoop(system: System, channel: Channel<GameState>) {
    var gameState = persistentListOf<Entity>()
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

        while(elapsed >= dt) {
            //elapsed = 0 ???
            elapsed -= dt
            ticks++
            gameState = system.operate(gameState)
            channel.send(gameState)
        }

        if (java.lang.System.currentTimeMillis() - timer >= 1000) {
            timer += 1000
            ticks = 0
        }
    }
}

private suspend fun renderLoop(renderer: Renderer, channel: ReceiveChannel<GameState>) {
    while(true) {
        var oldState : GameState? = null
        for (newState in channel) {
            if (oldState == newState) continue
            renderer.onRender(newState)
            oldState = newState
        }
    }
}