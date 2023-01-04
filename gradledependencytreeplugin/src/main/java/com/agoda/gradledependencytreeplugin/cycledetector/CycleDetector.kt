package com.agoda.gradledependencytreeplugin.cycledetector

import com.agoda.gradledependencytreeplugin.DependencyNode

//https://en.wikipedia.org/wiki/Cycle_(graph_theory)
//DFS(v):
//if finished(v)
//return
//if visited(v)
//"Cycle found" and return
//visited(v) = true
//for every neighbour w
//DFS(w)
//finished(v) = true
class CycleDetector {
    private val visitedNode = mutableSetOf<DependencyNode>()
    private val completedNodes = mutableSetOf<DependencyNode>()
    fun detectCycle(resolvedDependency: DependencyNode) {
        println("Checking for cycles in ${resolvedDependency.name}")
        transverseDependenciesForCycles(resolvedDependency)
    }

    private fun transverseDependenciesForCycles(resolvedDependency: DependencyNode) {
        if (completedNodes.contains(resolvedDependency)) return
        if (visitedNode.contains(resolvedDependency)) throw CycleDetectedException("Cycle detected in dependency ${resolvedDependency.name}")
        visitedNode.add(resolvedDependency)
        resolvedDependency.children.forEach {
            transverseDependenciesForCycles(it)
        }
        completedNodes.add(resolvedDependency)
    }
}

