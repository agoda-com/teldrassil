package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.config.ConfigParams
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.view.mxGraph
import java.awt.Graphics2D
import java.util.*

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
    private var layerGap: Double = 100.0
    private var nodeGap: Double = 20.0

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
        val shouldSkipInitialLayout = graphNodes.values.any { it.x != 0 || it.y != 0 }
        if (shouldSkipInitialLayout) {
            graphNodes.values.forEach {
                it.position(it.x, it.y)
            }
            return
        }

        val mxVertices = mutableMapOf<GraphNodeView, Any>()
        val mxGraph = mxGraph()
        val model = mxGraph.model.also { it.beginUpdate() }
        val parent = mxGraph.defaultParent

        graphNodes.values.forEach {
            val mxVertex = mxGraph.insertVertex(
                    parent,
                    UUID.randomUUID().toString(),
                    it,
                    it.x.toDouble(),
                    it.y.toDouble(),
                    it.width.toDouble(),
                    it.height.toDouble()
            )
            model.getGeometry(mxVertex).height = it.height.toDouble()
            model.getGeometry(mxVertex).width = it.width.toDouble()
            mxVertices[it] = mxVertex
        }

        graphEdges.forEach {
            mxGraph.insertEdge(
                    parent,
                    null,
                    null,
                    mxVertices[graphNodes[it.fromNodeId]],
                    mxVertices[graphNodes[it.toNodeId]]
            )
        }

        mxGraph.model.endUpdate()

        val layout = mxHierarchicalLayout(mxGraph).apply {
            interRankCellSpacing = 5.0
            interHierarchySpacing = 5.0
            intraCellSpacing = 5.0
        }
        layout.execute(mxGraph.defaultParent)

        graphNodes.values.forEach {
            val mxGeometry = model.getGeometry(mxVertices[it])
            it.position(
                    mxGeometry.point.x,
                    mxGeometry.point.y
            )
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