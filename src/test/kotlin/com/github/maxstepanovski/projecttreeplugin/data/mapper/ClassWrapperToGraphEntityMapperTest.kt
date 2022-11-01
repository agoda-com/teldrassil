package com.github.maxstepanovski.projecttreeplugin.data.mapper

import com.github.maxstepanovski.projecttreeplugin.data.model.NodeEntity
import com.github.maxstepanovski.projecttreeplugin.graph.BfsGraphBuilder
import com.github.maxstepanovski.projecttreeplugin.graph.ClassResolverFake
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ClassWrapperToGraphEntityMapperTest : BasePlatformTestCase() {

    fun testMapClassesWithCircularDependencies() {
        // given
        val classResolver = ClassResolverFake()
        val graphBuilder = BfsGraphBuilder(classResolver)
        val rootClassWrapper = graphBuilder.buildGraph("${ClassResolverFake.PACKAGE}${ClassResolverFake.PRODUCT_PRESENTER}")!!
        val mapper = ClassWrapperToGraphEntityMapper()

        // when
        val result = mapper.map(rootClassWrapper)

        // then
        assertEquals(result.rootNodeId, rootClassWrapper.id)
        assertEquals(result.nodes.size, 6)
        assertEquals(result.edges.size, 7)

        val productPresenterWrapper = rootClassWrapper
        val productPresenterNode = result.nodes[result.rootNodeId]!!
        assertCorrectMapping(productPresenterNode, productPresenterWrapper)

        val productPresenterImplWrapper = productPresenterWrapper.dependencies[0]
        val productPresenterImplNode = result.nodes[productPresenterImplWrapper.id]!!
        assertCorrectMapping(productPresenterImplNode, productPresenterImplWrapper)

        val productInteractorWrapper = productPresenterImplWrapper.dependencies.find { it.name == ClassResolverFake.PRODUCT_INTERACTOR }!!
        val bookingInteractorWrapper = productPresenterImplWrapper.dependencies.find { it.name == ClassResolverFake.BOOKING_INTERACTOR }!!
        val productInteractorNode = result.nodes[productInteractorWrapper.id]!!
        val bookingInteractorNode = result.nodes[bookingInteractorWrapper.id]!!
        assertCorrectMapping(productInteractorNode, productInteractorWrapper)
        assertCorrectMapping(bookingInteractorNode, bookingInteractorWrapper)

        val productInteractorImplWrapper = productInteractorWrapper.dependencies[0]
        val bookingInteractorImplWrapper = bookingInteractorWrapper.dependencies[0]
        val productInteractorImplNode = result.nodes[productInteractorImplWrapper.id]!!
        val bookingInteractorImplNode = result.nodes[bookingInteractorImplWrapper.id]!!
        assertCorrectMapping(productInteractorImplNode, productInteractorImplWrapper)
        assertCorrectMapping(bookingInteractorImplNode, bookingInteractorImplWrapper)

        assertEquals(productPresenterWrapper, productInteractorImplWrapper.dependencies[0])
        assertEquals(productPresenterWrapper, bookingInteractorImplWrapper.dependencies[0])
    }

    fun assertCorrectMapping(node: NodeEntity, classWrapper: ClassWrapper) {
        assertEquals(node.classType, classWrapper.type)
        assertEquals(node.name, classWrapper.name)
        assertEquals(node.fullClassName, classWrapper.fullClassName)
        assertEquals(node.fields.size, classWrapper.fields.size)
        assertEquals(node.methods.size, classWrapper.methods.size)
    }
}