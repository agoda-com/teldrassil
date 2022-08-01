package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper.Companion.NODE_HEIGHT
import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper.Companion.NODE_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.event.*
import javax.swing.JPanel


class DiagramPanel(
        private val graphView: GraphView
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {

    private var draggedNode: GraphNodeView? = null
    private var draggingPoint: Point? = null
    private var draggedDiffX: Int = 0
    private var draggedDiffY: Int = 0

    init {
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        with(graphView) {
            graphNodes.values.forEach {
                g.drawRoundRect(it.x, it.y, it.width, it.height, 10, 10)
                g.drawString(it.name, it.x + 10, it.y + 20)
            }
            graphEdges.forEach {
                g.drawLine(
                        graphNodes[it.fromNodeId]?.outEdgesX ?: 0,
                        graphNodes[it.fromNodeId]?.outEdgesY ?: 0,
                        graphNodes[it.toNodeId]?.inEdgesX ?: 0,
                        graphNodes[it.toNodeId]?.inEdgesY ?: 0
                )
            }
        }
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        println("mouseWheelMoved")
    }

    override fun mouseDragged(e: MouseEvent) {
        println("mouseDragged")
        // calculating offset for the dragged node + changing coords
        draggedNode?.let {
            val newX = e.x - draggedDiffX
            val newY = e.y - draggedDiffY

            it.x = newX
            it.y = newY
            it.inEdgesX = newX + NODE_WIDTH / 2
            it.inEdgesY = newY
            it.outEdgesX = newX + NODE_WIDTH / 2
            it.outEdgesY = newY + NODE_HEIGHT
            repaint()
            return
        }

        // calculating offset for pan dragging + updating coords for all nodes/edges
        draggingPoint?.let {
            val offsetX = e.x - it.x
            val offsetY = e.y - it.y

            graphView.graphNodes.values.forEach { node ->
                node.x += offsetX
                node.y += offsetY
                node.inEdgesX += offsetX
                node.inEdgesY += offsetY
                node.outEdgesX += offsetX
                node.outEdgesY += offsetY
            }

            draggingPoint = Point(e.x, e.y)
            repaint()
        }
    }

    override fun mouseMoved(e: MouseEvent) {
        println("mouseMoved")
    }

    override fun mouseClicked(e: MouseEvent) {
        println("mouseClicked")
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

    override fun mouseReleased(e: MouseEvent) {
        println("mouseReleased")
        // region release dragged node
        draggedNode = null
        draggedDiffX = 0
        draggedDiffY = 0
        // end region

        // region release pan dragging
        draggingPoint = null
        // end region
    }

    override fun mouseEntered(e: MouseEvent) {
        println("mouseEntered")
    }

    override fun mouseExited(e: MouseEvent) {
        println("mouseExited")
    }
}