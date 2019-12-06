package com.rafl.gem

import com.rafl.gem.core.*
import com.rafl.gem.gfx.getDefaultRenderer

fun main() {
    gameLoop(getDefaultRenderer(), Config(), chain(
        system { emptyState().also{println("1")} },
        system { emptyState().also{println("2")} }
    ))
}
