package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.Graphics2D

data class GraphView(
        private val rootNode: GraphNodeView,
        private val graphNodes: Map<String, GraphNodeView>,
        private val graphEdges: List<GraphEdgeView>
) : PaintableLayout {
    var x: Int = 0
        private set
    var y: Int = 0
        private set
    private var draggedNode: GraphNodeView? = null
    private var draggedDiffX: Int = 0
    private var draggedDiffY: Int = 0
    private var layerGap: Int = 100
    private var nodeGap: Int = 20

    override fun size(g: Graphics2D) {
        graphNodes.values.forEach {
            it.size(g)
        }
    }

    override fun position(newX: Int, newY: Int) {
        val xDiff = newX - x
        val yDiff = newY - y
        graphNodes.values.forEach {
            it.position(
                    it.x + xDiff,
                    it.y + yDiff
            )
        }
    }

    override fun paint(g: Graphics2D) {
        graphNodes.values.forEach {
            it.paint(g)
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

    override fun layout() {
        var currentX = x
        var currentY = y
        var layerHeight = 0
        var layerWidth = 0

        val positioned = mutableSetOf<String>().also { it.add(rootNode.id) }
        val deque = ArrayDeque<GraphNodeView?>()
        deque.addLast(rootNode)
        deque.addLast(null)
        rootNode.position(currentX, currentY)
        currentY += rootNode.height + layerGap

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

    fun mousePressed(eventX: Int, eventY: Int): Boolean {
        for (entry in graphNodes) {
            val node = entry.value
            if (eventX in node.x..node.x + node.width && eventY in node.y..node.y + node.height) {
                draggedNode = node
                draggedDiffX = eventX - node.x
                draggedDiffY = eventY - node.y
                return true
            }
        }
        return false
    }

    fun mouseReleased(): Boolean {
        draggedNode = null
        draggedDiffX = 0
        draggedDiffY = 0
        return true
    }

    fun mouseDragged(eventX: Int, eventY: Int): Boolean {
        draggedNode?.let {
            it.position(eventX - draggedDiffX, eventY - draggedDiffY)
            return true
        } ?: return false
    }
}