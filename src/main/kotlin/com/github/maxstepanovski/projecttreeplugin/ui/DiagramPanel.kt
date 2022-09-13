package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.config.ConfigParams
import com.github.maxstepanovski.projecttreeplugin.data.repository.DiagramRepository
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.*
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel
import kotlin.math.roundToInt

class DiagramPanel(val project: Project, virtualFile: VirtualFile) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private val diagramRepository = DiagramRepository(project)
    private val isFirstTime = AtomicBoolean(true)
    private val zoomInButton = Button("+")
    private val zoomOutButton = Button("-")
    private val connectionButton = Button("Centered on")
    private val bigNamesButton = Button("Big names")
    private val zoomFactors = mutableListOf<Pair<Double, AffineTransform>>().apply {
        listOf(0.03, 0.075, 0.15, 0.3, 0.6, 1.0).forEach {
            add(Pair(it, AffineTransform().apply { scale(it, it) }))
        }
    }

    private var graphView: GraphView
    private var draggingPoint: Point? = null
    private var zoomIndex = zoomFactors.lastIndex

    init {
        graphView = diagramRepository.readFromFile(virtualFile.path)
        add(zoomInButton)
        zoomInButton.addActionListener {
            if (zoomIndex + 1 in zoomFactors.indices) {
                zoomIndex += 1
                graphView.scale = zoomFactors[zoomIndex].first.toFloat()
                repaint()
            }
        }
        add(zoomOutButton)
        zoomOutButton.addActionListener {
            if (zoomIndex - 1 in zoomFactors.indices) {
                zoomIndex -= 1
                graphView.scale = zoomFactors[zoomIndex].first.toFloat()
                repaint()
            }
        }
        add(connectionButton)
        connectionButton.addActionListener {
            ConfigParams.CENTERED_CONNECTION = !ConfigParams.CENTERED_CONNECTION
            if (ConfigParams.CENTERED_CONNECTION) {
                connectionButton.label = "Centered on"
            } else {
                connectionButton.label = "Centered off"
            }
            repaint()
        }
        add(bigNamesButton)
        bigNamesButton.addActionListener {
            graphView.shouldRenderBigNames = !graphView.shouldRenderBigNames
            repaint()
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
        if (graphView.mouseReleased()) {
            diagramRepository.saveToFile(graphView)
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

    private fun Int.scale(): Int = (this / zoomFactors[zoomIndex].first).roundToInt()
}