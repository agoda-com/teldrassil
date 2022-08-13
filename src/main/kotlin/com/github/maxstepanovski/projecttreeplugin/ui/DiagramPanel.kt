package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.*
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel
import kotlin.math.roundToInt


class DiagramPanel(
        private val graphView: GraphView
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private val isFirstTime = AtomicBoolean(true)
    private val isZoomed = AtomicBoolean(false)

    private val zoomInButton = Button("+")
    private val zoomOutButton = Button("-")
    private val zoomFactors = listOf(
            MAG_0_25 to AffineTransform().apply { scale(MAG_0_25, MAG_0_25) },
            MAG_0_50 to AffineTransform().apply { scale(MAG_0_50, MAG_0_50) },
            MAG_0_75 to AffineTransform().apply { scale(MAG_0_75, MAG_0_75) },
            MAG_1_0 to AffineTransform()
    )

    private var draggingPoint: Point? = null
    private var zoomIndex = zoomFactors.lastIndex

    init {
        add(zoomInButton)
        zoomInButton.addActionListener {
            if (zoomIndex + 1 in zoomFactors.indices) {
                zoomIndex += 1
                isZoomed.set(true)
                repaint()
            }
        }
        add(zoomOutButton)
        zoomOutButton.addActionListener {
            if (zoomIndex - 1 in zoomFactors.indices) {
                zoomIndex -= 1
                isZoomed.set(true)
                repaint()
            }
        }
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        g2.transform(zoomFactors[zoomIndex].second)

        // only size and position components once
        if (isFirstTime.compareAndSet(true, false)) {
            graphView.size(g2)
            graphView.layout()
        }

        graphView.paint(g2)
    }

    override fun mousePressed(e: MouseEvent) {
        val scaledX = e.x.scale()
        val scaledY = e.y.scale()

        if (graphView.mousePressed(scaledX, scaledY)) {
            return
        }
        draggingPoint = Point(scaledX, scaledY)
    }

    override fun mouseDragged(e: MouseEvent) {
        val scaledX = e.x.scale()
        val scaledY = e.y.scale()

        if (graphView.mouseDragged(scaledX, scaledY)) {
            repaint()
            return
        }

        // calculating offset for pan dragging + updating coords for all nodes/edges
        draggingPoint?.let {
            val offsetX = scaledX - it.x
            val offsetY = scaledY - it.y

            graphView.position(
                    graphView.x + offsetX,
                    graphView.y + offsetY
            )

            draggingPoint = Point(scaledX, scaledY)
            repaint()
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        graphView.mouseReleased()
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
    }

    companion object {
        private const val MAG_0_25 = 0.25
        private const val MAG_0_50 = 0.5
        private const val MAG_0_75 = 0.75
        private const val MAG_1_0 = 1.0
    }

    private fun Int.scale(): Int = (this / zoomFactors[zoomIndex].first).roundToInt()
}