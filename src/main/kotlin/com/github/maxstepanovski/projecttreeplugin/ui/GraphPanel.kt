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
    private val needResize = AtomicBoolean(false)
    private var zoomIndex = zoomFactors.lastIndex / 2
    private var at: AffineTransform = AffineTransform()
    private var it: AffineTransform = AffineTransform()
    private var transformedPoint: Point2D = Point2D.Double(0.0, 0.0)
    private var translation = Point2D.Double(0.0, 0.0)
    private val translationReference = Point2D.Double(0.0, 0.0)
    private val draggingReference = Point2D.Double(0.0, 0.0)

    init {
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D

        val saveTransform: AffineTransform = g2.transform
        at = AffineTransform(saveTransform)
        at.translate(width.toDouble() / 2, height.toDouble() / 2)
        at.scale(zoomFactors[zoomIndex].first, zoomFactors[zoomIndex].first)
        at.translate(-width.toDouble() / 2, -height.toDouble() / 2)

        at.translate(translation.x, translation.y)

        g2.transform = at

        // only size and layout components once
        if (isFirstTime.compareAndSet(true, false)) {
            graphView.size(g2)
            graphView.layout()
            // translate viewport to the root node coordinates
            translation.setLocation(
                translation.x - graphView.rootNode.x.toDouble(),
                translation.y - graphView.rootNode.y.toDouble()
            )
        }

        if (needResize.compareAndSet(true, false)) {
            graphView.size(g)
        }

        graphView.paint(g2)

        g2.transform = saveTransform
    }

    override fun mousePressed(e: MouseEvent) {
        // first transform the mouse point to the pan and zoom coordinates
        try {
            transformedPoint = at.inverseTransform(e.point, null)
        } catch (te: NoninvertibleTransformException) {
            println(te)
        }

        if (e.clickCount == 2) {
            graphView.mouseDoubleClicked(transformedPoint.x.roundToInt(), transformedPoint.y.roundToInt(), project)
        } else if (graphView.mousePressed(transformedPoint.x.roundToInt(), transformedPoint.y.roundToInt())) {
            draggingReference.setLocation(transformedPoint)
        } else {
            translationReference.setLocation(transformedPoint)
        }

        it = at
    }

    override fun mouseDragged(e: MouseEvent) {
        // first transform the mouse point to the pan and zoom
        // coordinates. We must take care to transform by the
        // initial transform, not the updated transform, so that
        // both the initial reference point and all subsequent
        // reference points are measured against the same origin.
        try {
            transformedPoint = it.inverseTransform(e.point, null)
        } catch (te: NoninvertibleTransformException) {
            println(te)
        }

        if (graphView.mouseDragged()) {
            val deltaX = transformedPoint.x - draggingReference.x
            val deltaY = transformedPoint.y - draggingReference.y

            draggingReference.setLocation(transformedPoint)

            graphView.moveDraggedNode(deltaX.roundToInt(), deltaY.roundToInt())
        } else {
            // the size of the pan translations
            // are defined by the current mouse location subtracted
            // from the reference location
            val deltaX = transformedPoint.x - translationReference.x
            val deltaY = transformedPoint.y - translationReference.y

            // make the reference point be the new mouse point.
            translationReference.setLocation(transformedPoint)
            translation.setLocation(translation.x + deltaX, translation.y + deltaY)
        }
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
        needResize.set(true)
        graphView.increaseFontSize()
        repaint()
    }

    fun decreaseFontSize() {
        needResize.set(true)
        graphView.decreaseFontSize()
        repaint()
    }

    fun switchEdgeMode() {
        repaint()
    }

    fun zoomIn() {
        if (zoomIndex + 1 in zoomFactors.indices) {
            zoomIndex++
            repaint()
        }
    }

    fun zoomOut() {
        if (zoomIndex - 1 in zoomFactors.indices) {
            zoomIndex--
            repaint()
        }
    }
}