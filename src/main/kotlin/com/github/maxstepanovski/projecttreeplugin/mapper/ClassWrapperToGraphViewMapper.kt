package com.github.maxstepanovski.projecttreeplugin.mapper

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.ui.GraphEdgeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphNodeView
import com.github.maxstepanovski.projecttreeplugin.ui.GraphView
import java.util.*
import kotlin.collections.ArrayDeque

class ClassWrapperToGraphViewMapper {

    /**
     * @param classWrapper the root node of a graph
     */
    fun map(classWrapper: ClassWrapper): GraphView {
        val graphNodeViews = mutableMapOf<String, GraphNodeView>()
        val graphEdgeViews = mutableListOf<GraphEdgeView>()

        val rootNodeView = classWrapper.toNodeView().also { graphNodeViews[it.id] = it }
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(classWrapper)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            val nodeView = graphNodeViews[node.id] ?: node.toNodeView().also {
                graphNodeViews[it.id] = it
            }
            node.dependencies.forEach { childNode ->
                val childNodeView = graphNodeViews[childNode.id] ?: childNode.toNodeView().also {
                    graphNodeViews[it.id] = it
                }
                val graphEdgeView = GraphEdgeView(
                        UUID.randomUUID().toString(),
                        nodeView.id,
                        childNodeView.id
                )
                nodeView.childNodes.add(childNodeView)
                graphEdgeViews.add(graphEdgeView)
                deque.addLast(childNode)
            }
        }

        return GraphView(
                rootNodeView,
                graphNodeViews,
                graphEdgeViews
        )
    }

    private fun ClassWrapper.toNodeView() = GraphNodeView(
            id = id,
            name = name,
            fields = (constructorParameters + fields).fold(mutableListOf()) { acc, field ->
                acc.add("${field.identifier} ${field.type}")
                acc
            },
            methods = methods.fold(mutableListOf()) { acc, method ->
                acc.add("${method.identifier} ${method.returnType}")
                acc
            }
    )
}