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
        val deque = ArrayDeque<ClassWrapper?>()
        var currentX = 0
        var currentY = 0

        deque.addLast(classWrapper)
        deque.addLast(null)

        graphNodeViews[classWrapper.id] = GraphNodeView(
                id = classWrapper.id,
                name = classWrapper.name,
                x = currentX,
                y = currentY,
                width = NODE_WIDTH,
                height = NODE_HEIGHT,
                inEdgesX = currentX + NODE_WIDTH / 2,
                inEdgesY = currentY,
                outEdgesX = currentX + NODE_WIDTH / 2,
                outEdgesY = currentY + NODE_HEIGHT
        )
        currentY += (NODE_HEIGHT + Y_OFFSET)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            if (node == null) {
                if (deque.isNotEmpty()) {
                    deque.addLast(null)
                    currentX = 0
                    currentY += (NODE_HEIGHT + Y_OFFSET)
                }
                continue
            }
            node.dependencies.forEach { childNode ->
                deque.addLast(childNode)
                graphEdgeViews.add(GraphEdgeView(
                        id = UUID.randomUUID().toString(),
                        fromNodeId = node.id,
                        toNodeId = childNode.id
                ))
                graphNodeViews[childNode.id] = GraphNodeView(
                        id = childNode.id,
                        name = childNode.name,
                        x = currentX,
                        y = currentY,
                        width = NODE_WIDTH,
                        height = NODE_HEIGHT,
                        inEdgesX = currentX + NODE_WIDTH / 2,
                        inEdgesY = currentY,
                        outEdgesX = currentX + NODE_WIDTH / 2,
                        outEdgesY = currentY + NODE_HEIGHT
                )
                currentX += (NODE_WIDTH + X_OFFSET)
            }
        }

        return GraphView(
                graphNodeViews,
                graphEdgeViews
        )
    }

    companion object {
        const val NODE_WIDTH = 200
        const val NODE_HEIGHT = 100
        const val X_OFFSET = 20
        const val Y_OFFSET = 20
    }
}