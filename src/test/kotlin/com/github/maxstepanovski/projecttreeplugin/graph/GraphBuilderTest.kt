package com.github.maxstepanovski.projecttreeplugin.graph

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class GraphBuilderTest : BasePlatformTestCase() {

    fun testClassTreeWithCircularDependencies() {
        // given
        val graphBuilder = BfsGraphBuilder(ClassResolverFake())

        // when
        val result = graphBuilder.buildGraph("${ClassResolverFake.PACKAGE}${ClassResolverFake.PRODUCT_PRESENTER}")

        // then
        val productPresenter = result!!
        assertEquals(productPresenter.dependencies.size, 1)

        val productPresenterImpl = productPresenter.dependencies[0]
        assertEquals(productPresenterImpl.fullClassName, "${ClassResolverFake.PACKAGE}${ClassResolverFake.PRODUCT_PRESENTER_IMPL}")
        assertEquals(productPresenterImpl.dependencies.size, 2)

        val productInteractor = productPresenterImpl.dependencies.find { it.name == ClassResolverFake.PRODUCT_INTERACTOR }!!
        val bookingInteractor = productPresenterImpl.dependencies.find { it.name == ClassResolverFake.BOOKING_INTERACTOR }!!
        assertEquals(productInteractor.fullClassName, "${ClassResolverFake.PACKAGE}${ClassResolverFake.PRODUCT_INTERACTOR}")
        assertEquals(productInteractor.dependencies.size, 1)
        assertEquals(bookingInteractor.fullClassName, "${ClassResolverFake.PACKAGE}${ClassResolverFake.BOOKING_INTERACTOR}")
        assertEquals(bookingInteractor.dependencies.size, 1)

        val productInteractorImpl = productInteractor.dependencies[0]
        val bookingInteractorImpl = bookingInteractor.dependencies[0]
        assertEquals(productInteractorImpl.dependencies.size, 1)
        assertEquals(productInteractorImpl.dependencies[0], productPresenter)
        assertEquals(bookingInteractorImpl.dependencies.size, 1)
        assertEquals(bookingInteractorImpl.dependencies[0], productPresenter)
    }
}