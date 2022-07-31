package com.github.maxstepanovski.projecttreeplugin.mapper

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.ui.GraphNodeView

class ClassWrapperToGraphNodeViewMapper {

    fun map(classWrapper: ClassWrapper): List<GraphNodeView> {
        val result = mutableListOf<GraphNodeView>()
        val deque = ArrayDeque<ClassWrapper?>()
        var currentX = 0
        var currentY = 0

        deque.addLast(classWrapper)
        deque.addLast(null)

        result.add(GraphNodeView(
                classWrapper.name,
                currentX,
                currentY,
                NODE_WIDTH,
                NODE_HEIGHT
        ))
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
            node.dependencies.forEach {
                deque.addLast(it)
                result.add(GraphNodeView(
                        it.name,
                        currentX,
                        currentY,
                        NODE_WIDTH,
                        NODE_HEIGHT
                ))
                currentX += (NODE_WIDTH + X_OFFSET)
            }
        }

        return result
    }

    companion object {
        private const val NODE_WIDTH = 200
        private const val NODE_HEIGHT = 100
        private const val X_OFFSET = 20
        private const val Y_OFFSET = 20
    }
}