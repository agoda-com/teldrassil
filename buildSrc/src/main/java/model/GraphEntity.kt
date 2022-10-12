package com.github.maxstepanovski.projecttreeplugin.data.model

import com.google.gson.annotations.SerializedName
import model.NodeEntity

data class GraphEntity(
    @SerializedName("root_node_id")
        val rootNodeId: String,
    @SerializedName("nodes")
        val nodes: Map<String, NodeEntity>,
    @SerializedName("edges")
        val edges: List<EdgeEntity>
)
