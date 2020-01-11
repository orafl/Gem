package com.rafl.gem.core

import com.rafl.gem.gfx.Renderer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

fun gameLoop(renderer: Renderer, initialState: GameState, system: System)
        : Nothing = runBlocking {
    val shared = Channel<GameState>(Channel.CONFLATED)

    renderer.load()
    launch(Dispatchers.Default) {
        updateLoop(initialState, system, shared)
    }
    renderLoop(renderer, shared)
}

private suspend fun CoroutineScope.updateLoop(
    initialState: GameState, system: System, channel: Channel<GameState>)
{
    var gameState = initialState
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
            gameState = system(gameState)
            channel.send(gameState)
        }

        if (java.lang.System.currentTimeMillis() - timer >= 1000) {
            kotlin.io.println(ticks)
            timer += 1000
            ticks = 0
        }
    }
}

private suspend fun renderLoop(
    renderer: Renderer, channel: ReceiveChannel<GameState>) : Nothing
{
    while(true) {
        for (newState in channel) {
            renderer.render(newState)
        }
    }
}