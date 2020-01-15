package com.rafl.gem.utils

import com.rafl.gem.io.ComponentParser

internal suspend fun parseTransform(parser: ComponentParser): Transform {
    val pos = parser.findReferenceOrNode("position") {
        parseVectwo(parser.subParse(it))
    }
    val rot = parseFloat(parser.component["rotation"])
    val s = parseFloat(parser.component["scale"])

    return Transform(pos.await(), rot, s)
}

data class Transform(val position: Vectwo,
                val rotation: Float = 0f,
                val scale: Float = 1f)