package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

interface GraphBuilder {

    /**
     * builds class' dependencies graph, returns root node of the graph
     */
    fun buildGraph(fullName: String): ClassWrapper?
}