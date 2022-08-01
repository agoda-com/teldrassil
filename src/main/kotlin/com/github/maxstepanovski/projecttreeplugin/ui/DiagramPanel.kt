package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper.Companion.NODE_HEIGHT
import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper.Companion.NODE_WIDTH
import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import javax.swing.JPanel


class DiagramPanel(
        private val graphView: GraphView
) : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {

    private var draggedNode: GraphNodeView? = null
    private var draggedDiffX: Int = 0
    private var draggedDiffY: Int = 0

    init {
        addMouseWheelListener(this)
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
        for (entry in graphView.graphNodes) {
            val node = entry.value
            if (e.x in node.x..node.x + node.width && e.y in node.y..node.y + node.height) {
                draggedNode = node
                draggedDiffX = e.x - node.x
                draggedDiffY = e.y - node.y
                break
            }
        }
    }

    override fun mouseReleased(e: MouseEvent) {
        println("mouseReleased")
        draggedNode = null
        draggedDiffX = 0
        draggedDiffY = 0
    }

    override fun mouseEntered(e: MouseEvent) {
        println("mouseEntered")
    }

    override fun mouseExited(e: MouseEvent) {
        println("mouseExited")
    }
}