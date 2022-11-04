package com.github.maxstepanovski.projecttreeplugin.data.mapper


import com.github.maxstepanovski.contract.model.EdgeEntity
import com.github.maxstepanovski.contract.model.GraphEntity
import com.github.maxstepanovski.contract.model.NodeEntity
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import java.util.*
import kotlin.collections.ArrayDeque

class ClassWrapperToGraphEntityMapper {

    /**
     * @param classWrapper the root node of a graph
     */
    fun map(classWrapper: ClassWrapper): GraphEntity {
        val nodeEntities = mutableMapOf<String, NodeEntity>()
        val edgeEntities = mutableListOf<EdgeEntity>()

        val rootNodeEntity = classWrapper.toNodeEntity().also { nodeEntities[it.id] = it }
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(classWrapper)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            node.dependencies.forEach { childNode ->
                val childNodeEntity: NodeEntity
                if (nodeEntities.containsKey(childNode.id)) {
                    childNodeEntity = nodeEntities[childNode.id]!!
                } else {
                    childNodeEntity = childNode.toNodeEntity()
                    nodeEntities[childNodeEntity.id] = childNodeEntity
                    deque.addLast(childNode)
                }
                val nodeEntity = nodeEntities[node.id]!!
                val edgeEntity = EdgeEntity(
                        UUID.randomUUID().toString(),
                        nodeEntity.id,
                        childNodeEntity.id
                )
                edgeEntities.add(edgeEntity)
            }
        }

        return GraphEntity(
                rootNodeEntity.id,
                nodeEntities,
                edgeEntities
        )
    }

    private fun ClassWrapper.toNodeEntity() = NodeEntity(
            id = id,
            name = name,
            classType = type,
            fields = (constructorParameters + fields).fold(mutableListOf()) { acc, field ->
                acc.add("${field.identifier} : ${field.type}")
                acc
            },
            methods = methods.fold(mutableListOf()) { acc, method ->
                acc.add("${method.identifier}${if (method.arguments.isEmpty())"(...)" else "()"} : ${method.returnType}")
                acc
            },
            x = 0,
            y = 0,
            fullClassName
    )
}