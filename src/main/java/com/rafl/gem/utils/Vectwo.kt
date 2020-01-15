package com.rafl.gem.utils

import com.rafl.gem.io.ComponentParser

internal fun parseFloat(value: Any?)
    = when(value) {
        null -> 0f
        is Int -> value.toFloat()
        is Long -> value.toFloat()
        is Double -> value.toFloat()
        else -> value as Float
    }

internal fun parseVectwo(parser: ComponentParser)
        = Vectwo(parseFloat(
    parser.component["x"]), parseFloat(parser.component["y"]))

data class Vectwo(val x: Float, val y: Float) {
    operator fun plus(v: Vectwo) = Vectwo(x + v.x, y + v.y)
    operator fun minus(v: Vectwo) = Vectwo(x - v.x, y - v.y)
    infix fun to(v: Vectwo) = v - this
    operator fun times(s: Float) = Vectwo(s*x, s*y)
}