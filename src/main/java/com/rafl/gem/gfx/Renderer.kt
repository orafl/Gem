package com.rafl.gem.gfx

import com.rafl.gem.core.GameState

interface Renderer {
    suspend fun load() {}
    fun onRender(gameState: GameState)
}

fun getDefaultRenderer(): Renderer = //SwingRenderer()
object : Renderer {
    override fun onRender(gameState: GameState) {}
}