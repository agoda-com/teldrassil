package com.agoda.gradledependencytreeplugin

data class DependencyNode(val id: String, val name: String) {
    val children: MutableSet<DependencyNode> = mutableSetOf()
}