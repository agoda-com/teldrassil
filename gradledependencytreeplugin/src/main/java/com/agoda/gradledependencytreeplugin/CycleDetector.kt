package com.agoda.gradledependencytreeplugin

import org.gradle.api.artifacts.ResolvedDependency

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
    private val visitedNode = mutableSetOf<ResolvedDependency>()
    private val completedNodes = mutableSetOf<ResolvedDependency>()
    fun detectCycle(resolvedDependency: ResolvedDependency) {
        println("Checking for cycles in ${resolvedDependency.name}")
        transverseDependenciesForCycles(resolvedDependency)
    }

    private fun transverseDependenciesForCycles(resolvedDependency: ResolvedDependency) {
        if (completedNodes.contains(resolvedDependency)) return
        if (visitedNode.contains(resolvedDependency)) throw IllegalStateException("Cycle detected in dependency ${resolvedDependency.name}")
        visitedNode.add(resolvedDependency)
        resolvedDependency.children.forEach {
            transverseDependenciesForCycles(it)
        }
        completedNodes.add(resolvedDependency)
    }
}