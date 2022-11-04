package com.github.maxstepanovski.projecttreeplugin.data.mapper

import com.github.maxstepanovski.contract.model.NodeEntity
import com.github.maxstepanovski.projecttreeplugin.graph.BfsGraphBuilder
import com.github.maxstepanovski.projecttreeplugin.graph.ClassResolverFake
import com.github.maxstepanovski.projecttreeplugin.ui.GraphNodeView
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GraphEntityToGraphViewMapperTest: BasePlatformTestCase() {

    fun testMapClassesWithCircularDependencies() {
        // given
        val graphBuilder = BfsGraphBuilder(ClassResolverFake())
        val rootClassWrapper = graphBuilder.buildGraph("${ClassResolverFake.PACKAGE}${ClassResolverFake.PRODUCT_PRESENTER}")!!
        val helperMapper = ClassWrapperToGraphEntityMapper()
        val graphEntity = helperMapper.map(rootClassWrapper)
        val mapper = GraphEntityToGraphViewMapper()

        // when
        val result = mapper.map(graphEntity)

        // then
        assertEquals(result.graphNodes.size, graphEntity.nodes.size)
        assertEquals(result.graphEdges.size, graphEntity.edges.size)
        assertEquals(result.rootNode, result.graphNodes[rootClassWrapper.id])

        graphEntity.nodes.values.forEach {
            val graphNode = result.graphNodes[it.id]!!
            assertCorrectMapping(graphNode, it)
        }

        graphEntity.edges.forEach { edgeEntity ->
            val edge = result.graphEdges.find { edgeEntity.id == it.id }!!
            assertEquals(edge.fromNodeId, edgeEntity.from)
            assertEquals(edge.toNodeId, edgeEntity.to)
        }
    }

    private fun assertCorrectMapping(graphNodeView: GraphNodeView, graphEntity: NodeEntity) {
        assertEquals(graphNodeView.id, graphEntity.id)
        assertEquals(graphNodeView.name, graphEntity.name)
        assertEquals(graphNodeView.fullClassName, graphEntity.fullClassName)
        assertEquals(graphNodeView.methods, graphEntity.methods)
        assertEquals(graphNodeView.fields, graphEntity.fields)
        assertEquals(graphNodeView.classType, graphEntity.classType)
    }
}