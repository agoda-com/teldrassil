package com.github.maxstepanovski.projecttreeplugin.data.mapper

import com.github.maxstepanovski.contract.model.EdgeEntity
import com.github.maxstepanovski.contract.model.GraphEntity
import com.github.maxstepanovski.contract.model.NodeEntity
import com.github.maxstepanovski.projecttreeplugin.ui.GraphEdgeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphNodeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphView

class GraphViewToGraphEntityMapper {

    fun map(graphView: GraphView): GraphEntity {
        val nodeEntities = mutableMapOf<String, NodeEntity>()
        val edgeEntities = mutableListOf<EdgeEntity>()

        graphView.graphNodes.values.forEach {
            nodeEntities[it.id] = it.toNodeEntity()
        }

        graphView.graphEdges.forEach {
            edgeEntities.add(it.toEdgeEntity())
        }

        return GraphEntity(
                rootNodeId = graphView.rootNode.id,
                nodes = nodeEntities,
                edges = edgeEntities
        )
    }

    private fun GraphNodeView.toNodeEntity(): NodeEntity = NodeEntity(
            id = id,
            name = name,
            classType = classType,
            fields = fields,
            methods = methods,
            x = x,
            y = y,
            fullClassName
    )

    private fun GraphEdgeView.toEdgeEntity(): EdgeEntity = EdgeEntity(
            id = id,
            from = fromNodeId,
            to = toNodeId
    )
}