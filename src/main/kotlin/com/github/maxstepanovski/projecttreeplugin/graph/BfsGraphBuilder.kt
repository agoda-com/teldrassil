package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.projecttreeplugin.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

/**
 * bfs traverses dependencies
 */
class BfsGraphBuilder(
        private val classResolver: ClassResolver
) : GraphBuilder {

    override fun buildGraph(className: String): ClassWrapper? {
        val rootNode: ClassWrapper = classResolver.resolveClassByName(className) ?: return null
        val resolved = mutableMapOf<String, ClassWrapper>()
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(rootNode)
        resolved[rootNode.name] = rootNode

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            val fullDependenciesList = (node.constructorParameters + node.fields).toMutableList()
            if (node.type == ClassType.INTERFACE && node.directInheritors.size < 2) {
                fullDependenciesList += node.directInheritors
            }
            fullDependenciesList.forEach { dependency ->
                val fullName = dependency.fullName
                val shortName = fullName.split(".").last()
                if (resolved.containsKey(shortName).not()) {
                    classResolver.resolveClassByFullName(fullName)?.let { neighbor ->
                        resolved[neighbor.name] = neighbor
                        deque.addLast(neighbor)
                    }
                }
                resolved[shortName]?.let(node::addDependency)
            }
        }

        return rootNode
    }
}