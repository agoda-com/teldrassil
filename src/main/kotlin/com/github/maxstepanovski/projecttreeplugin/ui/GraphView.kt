package com.github.maxstepanovski.projecttreeplugin.ui

data class GraphView(
        val rootNode: GraphNodeView,
        val graphNodes: Map<String, GraphNodeView>,
        val graphEdges: List<GraphEdgeView>
)