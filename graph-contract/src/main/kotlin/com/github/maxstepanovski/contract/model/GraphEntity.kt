package com.github.maxstepanovski.contract.model

import com.google.gson.annotations.SerializedName

data class GraphEntity(
    @SerializedName("root_node_id")
        val rootNodeId: String,
    @SerializedName("nodes")
        val nodes: Map<String, NodeEntity>,
    @SerializedName("edges")
        val edges: List<EdgeEntity>
)
