package com.rafl.gem.gfx

import com.rafl.gem.core.GameState
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics2D
import javax.swing.JFrame
import kotlin.system.measureTimeMillis

class SwingRenderer : Renderer {
    private val canvas = Canvas()

    override suspend fun load() {
        val time = measureTimeMillis {
            canvas.setSize(640, 480)
            with(JFrame("Game")) {
                add(canvas)
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                isResizable = false
                pack()
                setLocationRelativeTo(null)
                isVisible = true
            }
        }

        println("Successfully loaded resources in $time ms")
    }
    override fun render(gameState: GameState) {
        val bs = canvas.bufferStrategy
            ?: run {
                canvas.createBufferStrategy(3)
                canvas.bufferStrategy
            }

        val g = bs.drawGraphics as Graphics2D

        g.color = Color.BLACK
        g.fillRect(0, 0, canvas.width, canvas.height)

        g.dispose()
        bs.show()
    }
}