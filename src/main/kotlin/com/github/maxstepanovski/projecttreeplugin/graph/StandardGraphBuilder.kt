package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

/**
 * bfs traverses dependencies
 */
class StandardGraphBuilder(
        private val classResolver: ClassResolver
) : GraphBuilder {

    override fun buildGraph(className: String): ClassWrapper? {
        val rootNode: ClassWrapper = classResolver.resolveClassByName(className) ?: return null
        val resolved = mutableMapOf<String, ClassWrapper>()
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(rootNode)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            (node.constructorParameters + node.fields).forEach { dependency ->
                val fullName = dependency.fullName
                (resolved[fullName] ?: (classResolver.resolveClassByFullName(fullName)
                        ?.also { resolved[fullName] = it }))
                        ?.let { childNode ->
                            node.addDependency(childNode)
                            deque.addLast(childNode)
                        }
            }
        }

        return rootNode
    }
}