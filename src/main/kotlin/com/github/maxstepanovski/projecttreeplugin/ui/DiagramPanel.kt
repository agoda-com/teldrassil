package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel


class DiagramPanel(
        private val graphView: GraphView
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private var draggingPoint: Point? = null
    private val isFirstTime = AtomicBoolean(true)

    init {
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        // only size and position components once
        if (isFirstTime.compareAndSet(true, false)) {
            graphView.size(g2)
            graphView.layout()
        }

        graphView.paint(g2)
    }

    override fun mousePressed(e: MouseEvent) {
        if (graphView.mousePressed(e)) {
            return
        }
        draggingPoint = Point(e.x, e.y)
    }

    override fun mouseDragged(e: MouseEvent) {
        if (graphView.mouseDragged(e)) {
            repaint()
            return
        }

        // calculating offset for pan dragging + updating coords for all nodes/edges
        draggingPoint?.let {
            val offsetX = e.x - it.x
            val offsetY = e.y - it.y

            graphView.position(
                    graphView.x + offsetX,
                    graphView.y + offsetY
            )

            draggingPoint = Point(e.x, e.y)
            repaint()
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        graphView.mouseReleased(e)
        draggingPoint = null
    }

    override fun mouseEntered(e: MouseEvent) {
    }

    override fun mouseExited(e: MouseEvent) {
    }

    override fun mouseMoved(e: MouseEvent) {
    }

    override fun mouseClicked(e: MouseEvent) {
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        p
    }
}