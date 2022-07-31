package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import javax.swing.JPanel


class DiagramPanel(
        private val nodes: List<GraphNodeView>
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
        nodes.forEach {
            g.drawRoundRect(it.x, it.y, it.width, it.height, 10, 10)
            g.drawString(it.name, it.x + 10, it.y + 20)
        }
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
        println("mouseWheelMoved")
    }

    override fun mouseDragged(e: MouseEvent) {
        println("mouseDragged")
        draggedNode?.let {
            it.x = e.x - draggedDiffX
            it.y = e.y - draggedDiffY
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
        for (node in nodes) {
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

data class GraphNodeView(
        val name: String,
        var x: Int,
        var y: Int,
        var width: Int,
        var height: Int
)