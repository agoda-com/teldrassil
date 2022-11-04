package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.contract.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

/**
 * bfs traverses dependencies
 */
class BfsGraphBuilder(
        private val classResolver: ClassResolver
) : GraphBuilder {

    override fun buildGraph(fullName: String): ClassWrapper? {
        val rootNode: ClassWrapper = classResolver.resolveClassByFullName(fullName) ?: return null
        val resolved = mutableMapOf<String, ClassWrapper>()
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(rootNode)
        resolved[rootNode.fullClassName] = rootNode

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            val fullDependenciesList = (node.constructorParameters + node.fields).toMutableList()
            if (node.type == ClassType.INTERFACE && node.directInheritors.size < 2) {
                fullDependenciesList += node.directInheritors
            }
            fullDependenciesList.forEach { dependency ->
                val dependencyFullName = dependency.fullName
                if (resolved.containsKey(dependencyFullName).not()) {
                    classResolver.resolveClassByFullName(dependencyFullName)?.let { neighbor ->
                        resolved[neighbor.fullClassName] = neighbor
                        deque.addLast(neighbor)
                    }
                }
                resolved[dependencyFullName]?.let(node::addDependency)
            }
        }

        return rootNode
    }
}