package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.contract.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import java.util.*

class ClassResolverFake : ClassResolver {
    private val classes = mutableMapOf<String, ClassWrapper>()

    init {
        classes.putAll(createClassSystemWithCircularDependencies())
    }

    override fun resolveClassByFullName(fullName: String): ClassWrapper? = classes[fullName]

    /**
     *           ------------- ProductPresenter (interface)----------------
     *         /                          |                                 \
     *        |                           |                                 |
     *        |            ProductPresenterImpl (class)                     |
     *        |            private val interactor: ProductInteractor        |
     *        |            private val interactor2: BookingInteractor       |
     *        |          /                                        \         |
     *        |         /                                          \        |
     *        |    ProductInteractor (interface)            BookingInteractor (interface)
     *        |        |                                            |       |
     *        |        |                                            |       |
     *      ProductInteractorImpl (class)                 BookingInteractorImpl (class)
     *      private val presenter: ProductPresenter       private val presenter: ProductPresenter
     */
    private fun createClassSystemWithCircularDependencies(): MutableMap<String, ClassWrapper> =
            mutableMapOf<String, ClassWrapper>().apply {
                put("${PACKAGE}${PRODUCT_PRESENTER}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = PRODUCT_PRESENTER,
                        type = ClassType.INTERFACE,
                        constructorParameters = emptyList(),
                        fields = emptyList(),
                        methods = emptyList(),
                        directInheritors = listOf(
                                ValueParameter(listOf(), "", PRODUCT_PRESENTER_IMPL, "${PACKAGE}${PRODUCT_PRESENTER_IMPL}")
                        ),
                        fullClassName = "${PACKAGE}${PRODUCT_PRESENTER}"
                ))
                put("${PACKAGE}${PRODUCT_PRESENTER_IMPL}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = PRODUCT_PRESENTER_IMPL,
                        type = ClassType.CLASS,
                        constructorParameters = emptyList(),
                        fields = listOf(
                                ValueParameter(listOf("private"), "interactor", PRODUCT_INTERACTOR, "${PACKAGE}${PRODUCT_INTERACTOR}"),
                                ValueParameter(listOf("private"), "interactor2", BOOKING_INTERACTOR, "${PACKAGE}${BOOKING_INTERACTOR}")
                        ),
                        methods = emptyList(),
                        directInheritors = emptyList(),
                        fullClassName = "${PACKAGE}${PRODUCT_PRESENTER_IMPL}"
                ))
                put("${PACKAGE}${PRODUCT_INTERACTOR}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = PRODUCT_INTERACTOR,
                        type = ClassType.INTERFACE,
                        constructorParameters = emptyList(),
                        fields = listOf(),
                        methods = emptyList(),
                        directInheritors = listOf(
                                ValueParameter(listOf(), "", PRODUCT_INTERACTOR_IMPL, "${PACKAGE}${PRODUCT_INTERACTOR_IMPL}")
                        ),
                        fullClassName = "${PACKAGE}${PRODUCT_INTERACTOR}"
                ))
                put("${PACKAGE}${PRODUCT_INTERACTOR_IMPL}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = PRODUCT_INTERACTOR_IMPL,
                        type = ClassType.CLASS,
                        constructorParameters = emptyList(),
                        fields = listOf(
                                ValueParameter(listOf("private"), "presenter", PRODUCT_PRESENTER, "${PACKAGE}${PRODUCT_PRESENTER}")
                        ),
                        methods = emptyList(),
                        directInheritors = emptyList(),
                        fullClassName = "${PACKAGE}${PRODUCT_INTERACTOR_IMPL}"
                ))
                put("${PACKAGE}${BOOKING_INTERACTOR}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = BOOKING_INTERACTOR,
                        type = ClassType.INTERFACE,
                        constructorParameters = emptyList(),
                        fields = listOf(),
                        methods = emptyList(),
                        directInheritors = listOf(
                                ValueParameter(listOf(), "", BOOKING_INTERACTOR_IMPL, "${PACKAGE}${BOOKING_INTERACTOR_IMPL}")
                        ),
                        fullClassName = "${PACKAGE}${BOOKING_INTERACTOR}"
                ))
                put("${PACKAGE}${BOOKING_INTERACTOR_IMPL}", ClassWrapper(
                        id = UUID.randomUUID().toString(),
                        name = BOOKING_INTERACTOR_IMPL,
                        type = ClassType.CLASS,
                        constructorParameters = emptyList(),
                        fields = listOf(
                                ValueParameter(listOf("private"), "presenter", PRODUCT_PRESENTER, "${PACKAGE}${PRODUCT_PRESENTER}")
                        ),
                        methods = emptyList(),
                        directInheritors = emptyList(),
                        fullClassName = "${PACKAGE}${BOOKING_INTERACTOR_IMPL}"
                ))
            }

    companion object {
        const val PACKAGE = "com.example."
        const val PRODUCT_PRESENTER = "ProductPresenter"
        const val PRODUCT_PRESENTER_IMPL = "ProductPresenterImpl"
        const val PRODUCT_INTERACTOR = "ProductInteractor"
        const val PRODUCT_INTERACTOR_IMPL = "ProductInteractorImpl"
        const val BOOKING_INTERACTOR = "BookingInteractor"
        const val BOOKING_INTERACTOR_IMPL = "BookingInteractorImpl"
    }
}