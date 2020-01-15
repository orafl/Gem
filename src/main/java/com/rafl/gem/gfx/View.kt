package com.rafl.gem.gfx

import com.rafl.gem.io.ComponentParser
import com.rafl.gem.utils.Transform
import com.rafl.gem.utils.parseTransform
import com.rafl.gem.utils.parseVectwo
import java.lang.IllegalArgumentException

internal suspend fun parseView(parser: ComponentParser): View {
    val sprite = parser.findReferenceOrNode("show") {
        parseSprite(parser.subParse(it))
    }

    return try {
        val xform = parser.findReferenceOrNode("at") {
            parseTransform(parser.subParse(it))
        }
        View(sprite.await(), xform.await())
    } catch(e: IllegalArgumentException) {
        val xform = parser.findReferenceOrNode("at") {
            parseVectwo(parser.subParse(it))
        }
        View(sprite.await(), Transform(xform.await()))
    }
}

data class View(val sprite: Sprite, val transform: Transform)