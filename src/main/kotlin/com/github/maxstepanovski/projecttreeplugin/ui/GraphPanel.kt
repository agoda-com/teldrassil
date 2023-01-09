package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.data.repository.DiagramRepository
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.*
import java.awt.geom.AffineTransform
import java.awt.geom.NoninvertibleTransformException
import java.awt.geom.Point2D
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JPanel
import kotlin.math.roundToInt


class GraphPanel(
        private val project: Project,
        private val diagramRepository: DiagramRepository,
        virtualFile: VirtualFile
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private val graphView: GraphView = diagramRepository.readFromFile(virtualFile.path)
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
                scale = zoomFactors[value].first
                repaint()
            }
        }

    var at: AffineTransform = AffineTransform()
    var transformedPoint: Point2D = Point2D.Double(0.0, 0.0)
    var translateX = 0.0
    var translateY = 0.0
    var scale = zoomFactors[zoomFactors.size / 2].first
    var referenceX = 0.0
    var referenceY = 0.0
    var initialTransform: AffineTransform = AffineTransform()

    init {
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        // only size and layout components once
        if (isFirstTime.compareAndSet(true, false)) {
            graphView.size(g2)
            graphView.layout()
            // translate viewport to the root node coordinates
            translateX = -graphView.rootNode.x.toDouble()
            translateY = -graphView.rootNode.y.toDouble()
        }

        val saveTransform: AffineTransform = g2.transform
        at = AffineTransform(saveTransform)
        at.translate(width.toDouble() /2, height.toDouble() /2)
        at.scale(scale, scale)
        at.translate(-width.toDouble()/2, -height.toDouble()/2)

        at.translate(translateX, translateY);

        g2.transform = at

        graphView.paint(g2)

        g2.transform = saveTransform

//        println("reference point: x = ${referenceX} ; y = ${referenceY}")
//        println("viewport offset: x = ${translateX} ; y = ${translateY}")
    }

    override fun mousePressed(e: MouseEvent) {
        // first transform the mouse point to the pan and zoom coordinates
        try {
            transformedPoint = at.inverseTransform(e.point, null)
        } catch (te: NoninvertibleTransformException) {
            println(te)
        }

        println("viewport: x = ${e.x} | y = ${e.y}")
        println("original: x = ${transformedPoint.x} | y = ${transformedPoint.y}")

        val isDoubleClick = e.clickCount == 2
        if (isDoubleClick) {
            graphView.mouseDoubleClicked(transformedPoint.x.roundToInt(), transformedPoint.y.roundToInt(), project)
            return
        }
        if (graphView.mousePressed(transformedPoint.x.roundToInt(), transformedPoint.y.roundToInt())) {
            return
        }

        // save the transformed starting point and the initial transform
        referenceX = transformedPoint.x
        referenceY = transformedPoint.y
        initialTransform = at
    }

    override fun mouseDragged(e: MouseEvent) {
        // first transform the mouse point to the pan and zoom
        // coordinates. We must take care to transform by the
        // initial transform, not the updated transform, so that
        // both the initial reference point and all subsequent
        // reference points are measured against the same origin.
        try {
            transformedPoint = initialTransform.inverseTransform(e.point, null)
        } catch (te: NoninvertibleTransformException) {
            println(te)
        }

        if (graphView.mouseDragged(transformedPoint.x.roundToInt(), transformedPoint.y.roundToInt())) {
            repaint()
            return
        }

        // the size of the pan translations
        // are defined by the current mouse location subtracted
        // from the reference location
        val deltaX = transformedPoint.x - referenceX
        val deltaY = transformedPoint.y - referenceY

        // make the reference point be the new mouse point.
        referenceX = transformedPoint.x
        referenceY = transformedPoint.y

        translateX += deltaX
        translateY += deltaY

        repaint()
    }

    override fun mouseReleased(e: MouseEvent) {
        if (graphView.mouseReleased()) {
            diagramRepository.saveToFile(graphView)
            return
        }
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