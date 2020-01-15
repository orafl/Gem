package com.rafl.gem.gfx

import com.rafl.gem.io.Asset
import com.rafl.gem.io.ComponentParser
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

internal fun parseSprite(parser: ComponentParser): Sprite {
    val src = parser.component["src"] as String
    val x = parser.component["x"] as Int? ?: 0
    val y = parser.component["y"] as Int? ?: 0
    val w = parser.component["width"] as Int? ?: -1
    val h = parser.component["height"] as Int? ?: -1
    return Sprite.from(src, x, y, w, h)
}

class Sprite(private val img: Asset<BufferedImage>) {
    val asBufferedImage get() = img.get()

    companion object {
        private val cache = HashMap<String, BufferedImage>()

        private fun load(src: String) = cache[src] ?: run {
            val img = ImageIO.read(Any::class.java.getResourceAsStream(src))
            cache[src] = img; img
        }

        fun from(src: String, x: Int = 0, y: Int = 0, w: Int = -1, h: Int = -1)
        = Sprite(Asset.load {
            val img = load(src)
            img.getSubimage(x, y,
                if (w < 0) img.width else w, if (h < 0) img.height else h)
        })
    }
}