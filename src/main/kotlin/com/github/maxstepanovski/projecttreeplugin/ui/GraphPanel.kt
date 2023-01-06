package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.data.repository.DiagramRepository
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel
import kotlin.math.roundToInt

class GraphPanel(
        private val project: Project,
        private val diagramRepository: DiagramRepository,
        virtualFile: VirtualFile
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private val graphView: GraphView = diagramRepository.readFromFile(virtualFile.path)
    private var draggingPoint: Point? = null
    private val zoomFactors = mutableListOf<Pair<Double, AffineTransform>>().apply {
        listOf(0.03, 0.075, 0.15, 0.3, 0.6, 1.0).forEach {
            add(Pair(it, AffineTransform().apply { scale(it, it) }))
        }
    }
    private val isFirstTime = AtomicBoolean(true)
    var zoomIndex = zoomFactors.lastIndex
        set(value) {
            if (value in zoomFactors.indices) {
                field = value
                repaint()
            }
        }

    init {
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
        if (isFirstTime.get()) {
            graphView.size(g2)
            graphView.layout()
            graphView.position(0, 0)
        }

        graphView.paint(g2)
    }

    override fun mousePressed(e: MouseEvent) {
        val scaledX = e.x.scale()
        val scaledY = e.y.scale()

        val isDoubleClick = e.clickCount == 2

        if (isDoubleClick) {
            graphView.mouseDoubleClicked(scaledX, scaledY, project)
            return
        }
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
        diagramRepository.saveToFile(graphView)
        if (graphView.mouseReleased()) {
            return
        }
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

    fun increaseFontSize() {
        graphView.increaseFontSize()
        repaint()
    }

    fun decreaseFontSize() {
        graphView.decreaseFontSize()
        repaint()
    }

    fun switchEdgeMode() {
        repaint()
    }

    private fun Int.scale(): Int = (this / zoomFactors[zoomIndex].first).roundToInt()
}