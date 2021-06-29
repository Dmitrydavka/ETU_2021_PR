package com.zetcode

import java.awt.BasicStroke
import java.awt.EventQueue
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JFrame
import javax.swing.JPanel

internal class DrawPanel : JPanel() {
    private fun doDrawing(g: Graphics) {
        val g2d = g as Graphics2D
        val dash1 = floatArrayOf(2f, 0f, 2f)
        val dash2 = floatArrayOf(1f, 1f, 1f)
        val dash3 = floatArrayOf(4f, 0f, 2f)
        val dash4 = floatArrayOf(4f, 4f, 1f)
        g2d.drawLine(20, 40, 250, 40)
        val bs1 = BasicStroke(
            1F, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1.0f, dash1, 2f
        )
        val bs2 = BasicStroke(
            1F, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1.0f, dash2, 2f
        )
        val bs3 = BasicStroke(
            1F, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1.0f, dash3, 2f
        )
        val bs4 = BasicStroke(
            1F, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND, 1.0f, dash4, 2f
        )
        g2d.stroke = bs1
        g2d.drawLine(20, 80, 250, 80)
        g2d.stroke = bs2
        g2d.drawLine(20, 120, 250, 120)
        g2d.stroke = bs3
        g2d.drawLine(20, 160, 250, 160)
        g2d.stroke = bs4
        g2d.drawLine(20, 200, 250, 200)
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        doDrawing(g)
    }
}

class LinesEx : JFrame() {
    private fun initUI() {
        val drawPanel = DrawPanel()
        add(drawPanel)
        setSize(280, 270)
        title = "Lines"
        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    init {
        initUI()
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        val ex = LinesEx()
        ex.isVisible = true
    }
}