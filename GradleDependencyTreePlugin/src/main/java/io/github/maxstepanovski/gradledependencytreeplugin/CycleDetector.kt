package io.github.maxstepanovski.gradledependencytreeplugin

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
    val visitedNode = mutableSetOf<ResolvedDependency>()
    val finished = mutableSetOf<ResolvedDependency>()
    fun detectCycle(resolvedDependency: ResolvedDependency) {
        println("Checking for cycles in ${resolvedDependency.name}")
        dfs(resolvedDependency)
    }

    private fun dfs(resolvedDependency: ResolvedDependency) {
        if (finished.contains(resolvedDependency)) return
        if (visitedNode.contains(resolvedDependency)) throw IllegalStateException("Cycle detected in dependency ${resolvedDependency.name}")
        visitedNode.add(resolvedDependency)
        resolvedDependency.children.forEach { 
            dfs(it)
        }
        finished.add(resolvedDependency)
    }
}