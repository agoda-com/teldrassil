package com.github.maxstepanovski.projecttreeplugin.ui

data class GraphNodeView(
        val id: String,
        val name: String,
        var x: Int,
        var y: Int,
        var width: Int,
        var height: Int,
        var inEdgesX: Int,
        var inEdgesY: Int,
        var outEdgesX: Int,
        var outEdgesY: Int
)
