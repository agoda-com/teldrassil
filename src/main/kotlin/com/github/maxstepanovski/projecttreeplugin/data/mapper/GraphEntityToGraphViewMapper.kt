package com.github.maxstepanovski.projecttreeplugin.data.mapper

import com.github.maxstepanovski.contract.model.EdgeEntity
import com.github.maxstepanovski.contract.model.GraphEntity
import com.github.maxstepanovski.contract.model.NodeEntity
import com.github.maxstepanovski.projecttreeplugin.ui.GraphEdgeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphNodeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphView

class GraphEntityToGraphViewMapper {

    fun map(graphEntity: GraphEntity): GraphView {
        val nodeViews = mutableMapOf<String, GraphNodeView>()
        val edgeViews = mutableListOf<GraphEdgeView>()

        graphEntity.edges.forEach {
            edgeViews.add(it.toGraphEdgeView())
        }

        graphEntity.nodes.values.forEach { nodeEntity ->
            nodeViews[nodeEntity.id] = nodeEntity.toGraphNodeView().also {
                it.position(nodeEntity.x, nodeEntity.y)
            }
        }

        edgeViews.forEach {
            val from = nodeViews[it.fromNodeId]
            val to = nodeViews[it.toNodeId]
            if (from != null && to != null) {
                from.childNodes.add(to)
            }
        }

        return GraphView(
                rootNode = nodeViews[graphEntity.rootNodeId]!!,
                graphNodes = nodeViews,
                graphEdges = edgeViews
        )
    }

    private fun EdgeEntity.toGraphEdgeView() = GraphEdgeView(
            id = id,
            fromNodeId = from,
            toNodeId = to
    )

    private fun NodeEntity.toGraphNodeView() = GraphNodeView(
            id = id,
            name = name,
            classType = classType,
            fields = emptyList(),
            methods = emptyList(),
            fullClassName
    )
}