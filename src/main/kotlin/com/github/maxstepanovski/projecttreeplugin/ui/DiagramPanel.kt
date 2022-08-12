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
) : JPanel(), Paintable, MouseWheelListener, MouseListener, MouseMotionListener {

    private var draggedNode: GraphNodeView? = null
    private var draggingPoint: Point? = null
    private var draggedDiffX: Int = 0
    private var draggedDiffY: Int = 0
    private var layerGap: Int = 100
    private var nodeGap: Int = 20
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
            size(g2)
            initialPosition()
        }

        paint(g2)
    }

    override fun mouseDragged(e: MouseEvent) {
        println("mouseDragged")
        // calculating offset for the dragged node + changing coords
        draggedNode?.let {
            it.position(e.x - draggedDiffX, e.y - draggedDiffY)
            repaint()
            return
        }

        // calculating offset for pan dragging + updating coords for all nodes/edges
        draggingPoint?.let {
            val offsetX = e.x - it.x
            val offsetY = e.y - it.y

            graphView.graphNodes.values.forEach { node ->
                node.position(node.x + offsetX, node.y + offsetY)
            }

            draggingPoint = Point(e.x, e.y)
            repaint()
        }
    }

    override fun mousePressed(e: MouseEvent) {
        println("mousePressed")
        // Checking if any node is pressed
        for (entry in graphView.graphNodes) {
            val node = entry.value
            if (e.x in node.x..node.x + node.width && e.y in node.y..node.y + node.height) {
                draggedNode = node
                draggedDiffX = e.x - node.x
                draggedDiffY = e.y - node.y
                break
            }
        }
        // If no node is pressed then it's a pan dragging
        if (draggedNode == null) {
            draggingPoint = Point(e.x, e.y)
        }
    }

    override fun size(g: Graphics2D) {
        graphView.graphNodes.values.forEach {
            it.size(g)
        }
    }

    override fun position(newX: Int, newY: Int) {
    }

    override fun paint(g: Graphics2D) {
        graphView.graphNodes.values.forEach {
            it.paint(g)
        }
        graphView.graphEdges.forEach {
            g.drawLine(
                    graphView.graphNodes[it.fromNodeId]?.outEdgesX ?: 0,
                    graphView.graphNodes[it.fromNodeId]?.outEdgesY ?: 0,
                    graphView.graphNodes[it.toNodeId]?.inEdgesX ?: 0,
                    graphView.graphNodes[it.toNodeId]?.inEdgesY ?: 0
            )
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        println("mouseReleased")
        // region release dragged node
        draggedNode = null
        draggedDiffX = 0
        draggedDiffY = 0
        // end region

        draggingPoint = null
    }

    private fun initialPosition() {
        var currentX = 0
        var currentY = 0
        var layerHeight = 0
        var layerWidth = 0

        val positioned = mutableSetOf<String>().also { it.add(graphView.rootNode.id) }
        val deque = ArrayDeque<GraphNodeView?>()
        deque.addLast(graphView.rootNode)
        deque.addLast(null)
        graphView.rootNode.position(currentX, currentY)
        currentY += graphView.rootNode.height + layerGap

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            if (node == null) {
                if (deque.isNotEmpty()) {
                    deque.addLast(null)
                    currentX = draggedDiffX
                    currentY += draggedDiffY + layerHeight + layerGap
                    layerHeight = 0
                    layerWidth = 0
                }
                continue
            }
            node.childNodes.forEach { childNode ->
                if (positioned.contains(childNode.id).not()) {
                    childNode.position(currentX, currentY)
                    currentX += childNode.width + nodeGap
                    layerHeight = Integer.max(layerHeight, childNode.height)
                    layerWidth += childNode.width
                    positioned.add(childNode.id)
                    deque.addLast(childNode)
                }
            }
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
}