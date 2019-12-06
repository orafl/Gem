package com.rafl.gem.core

typealias System = (Config, GameState) -> GameState

inline fun system(crossinline s: Config.(GameState) -> GameState)
        : System = { config, state -> config.s(state) }

abstract class SystemClass : System {
    protected abstract val op: System
    override fun invoke(config: Config, state: GameState) = op(config, state)
}

fun chain(vararg systems: System) = system { s ->
    systems.fold(s) { acc, sys -> sys(this, acc) }
}