package com.rafl.gem.core

@FunctionalInterface
interface System {
    fun operate(gameState: GameState): GameState
}

class SystemSet : System {
    override fun operate(gameState: GameState): GameState {
        TODO("Collection of systems.")
    }
}