package com.rafl.gem.gfx

import com.rafl.gem.core.GameState

interface Renderer {
    suspend fun load() {}
    fun render(gameState: GameState)
    var antialias: Boolean
}

val noRender get() = object : Renderer {
    override var antialias = false
    override fun render(gameState: GameState) {}
}