package com.rafl.gem.gfx

import com.rafl.gem.core.GameState
import com.rafl.gem.core.getAll
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.VolatileImage
import javax.swing.JFrame

class SwingRenderer(width: Int, height: Int, override var antialias: Boolean)
    : Renderer {
    private val canvas = Canvas().apply { setSize(width, height) }

    private var bufferGraphics: Graphics2D? = null
    private var buffer: VolatileImage? = null
        get() {
            if (field == null || field?.validate(canvas.graphicsConfiguration)
                == VolatileImage.IMAGE_INCOMPATIBLE) {
                field = canvas.createVolatileImage(canvas.width, canvas.height)
                bufferGraphics = field?.createGraphics()
                bufferGraphics?.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    if (antialias) RenderingHints.VALUE_ANTIALIAS_ON
                    else RenderingHints.VALUE_ANTIALIAS_OFF)
            }
            return field
        }

    override suspend fun load() {
        with(JFrame("Game")) {
            add(canvas)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isResizable = false
            pack()
            setLocationRelativeTo(null)
            isVisible = true
        }
    }
    override fun render(gameState: GameState) {
        val bs = canvas.bufferStrategy
            ?: run {
                canvas.createBufferStrategy(3)
                canvas.bufferStrategy
            }

        val g = bs.drawGraphics as Graphics2D
        val bg = buffer.run { bufferGraphics } ?: return
        val views = gameState.flatMap { it.getAll<View>() }

        do {
            bg.color = Color.BLACK
            bg.fillRect(0, 0, canvas.width, canvas.height)
            views.forEach {
                val img = it.sprite.asBufferedImage ?: return
                val x = it.transform.position.x.toInt()
                val y = it.transform.position.y.toInt()
                val s = it.transform.scale.toDouble()

                AffineTransform.getRotateInstance(it.transform
                    .rotation.toDouble(), img.width/2.0, img.height/2.0).run {
                    AffineTransformOp(this, AffineTransformOp.TYPE_BILINEAR)
                }.apply {
                    bg.drawImage(filter(img, null), x, y,
                        (img.width*s).toInt(), (img.height*s).toInt(), null)
                }
            }
            g.drawImage(buffer, 0, 0, canvas.width, canvas.height, null)
        } while (buffer?.contentsLost() == true)

        g.dispose()
        bs.show()

        Toolkit.getDefaultToolkit().sync()
    }
}