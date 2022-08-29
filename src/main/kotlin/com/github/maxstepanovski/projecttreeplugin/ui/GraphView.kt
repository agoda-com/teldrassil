package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.config.ConfigParams
import graph.elements.impl.GraphEdge
import graph.elements.impl.GraphVertex
import graph.layout.GraphLayoutProperties
import graph.layout.LayoutAlgorithms
import graph.layout.Layouter
import graph.layout.PropertyEnums
import java.awt.Dimension
import java.awt.Graphics2D
import kotlin.math.roundToInt

data class GraphView(
        val rootNode: GraphNodeView,
        val graphNodes: Map<String, GraphNodeView>,
        val graphEdges: List<GraphEdgeView>
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
            val fromNode = graphNodes[it.fromNodeId]
            val toNode = graphNodes[it.toNodeId]
            if (fromNode != null && toNode != null) {
                if (ConfigParams.CENTERED_CONNECTION) {
                    drawCenteredConnection(fromNode, toNode, g)
                } else {
                    drawConnection(fromNode, toNode, g)
                }
            }
        }
    }

    override fun layout() {
        // If at least one node has a position different from the default x=0;y=0
        // means that it has been rendered and positioned before.
        // Hence, only need to pass existing coordinates for positioning
        val algorithm = LayoutAlgorithms.HIERARCHICAL
        val properties = GraphLayoutProperties()
        properties.setProperty(PropertyEnums.HierarchicalProperties.INTRA_CELL_SPACING, 100.0)
        properties.setProperty(PropertyEnums.HierarchicalProperties.INTER_RANK_CELL_SPACING, 100.0)

        val verticesMap = graphNodes.values.associateWith { GraphVertex(Dimension(it.width, it.height)) }.toMutableMap()
        val edgesList = graphEdges.asSequence().map {
            val fromNode = graphNodes[it.fromNodeId]
            val toNode = graphNodes[it.toNodeId]
            GraphEdge(
                    verticesMap[fromNode],
                    verticesMap[toNode]
            )
        }.toMutableList()

        val layouter = Layouter<GraphVertex, GraphEdge>(
                verticesMap.values.toMutableList(),
                edgesList,
                algorithm,
                properties
        )

        val drawing = layouter.layout()
        val vertexPositions = drawing.vertexMappings
        graphNodes.values.forEach {
            it.position(
                    vertexPositions[verticesMap[it]]?.x?.roundToInt() ?: 0,
                    vertexPositions[verticesMap[it]]?.y?.roundToInt() ?: 0,
            )
        }
//
//        val shouldSkipInitialLayout = graphNodes.values.any { it.x != 0 || it.y != 0 }
//        if (shouldSkipInitialLayout) {
//            graphNodes.values.forEach {
//                it.position(it.x, it.y)
//            }
//            return
//        }
//
//        // If all coords are default then create initial layout
//        var currentX = x
//        var currentY = y
//        var layerHeight = 0
//        var layerWidth = 0
//
//        val positioned = mutableSetOf<String>().also { it.add(rootNode.id) }
//        val deque = ArrayDeque<GraphNodeView?>()
//        deque.addLast(rootNode)
//        deque.addLast(null)
//        rootNode.position(currentX, currentY)
//        currentY += rootNode.height + layerGap
//
//        while (deque.isNotEmpty()) {
//            val node = deque.removeFirst()
//            if (node == null) {
//                if (deque.isNotEmpty()) {
//                    deque.addLast(null)
//                    currentX = draggedDiffX
//                    currentY += draggedDiffY + layerHeight + layerGap
//                    layerHeight = 0
//                    layerWidth = 0
//                }
//                continue
//            }
//            node.childNodes.forEach { childNode ->
//                if (positioned.contains(childNode.id).not()) {
//                    childNode.position(currentX, currentY)
//                    currentX += childNode.width + nodeGap
//                    layerHeight = Integer.max(layerHeight, childNode.height)
//                    layerWidth += childNode.width
//                    positioned.add(childNode.id)
//                    deque.addLast(childNode)
//                }
//            }
//        }
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
        if (draggedNode != null) {
            draggedNode = null
            draggedDiffX = 0
            draggedDiffY = 0
            return true
        }
        return false
    }

    fun mouseDragged(eventX: Int, eventY: Int): Boolean {
        draggedNode?.let {
            it.position(eventX - draggedDiffX, eventY - draggedDiffY)
            return true
        } ?: return false
    }
}