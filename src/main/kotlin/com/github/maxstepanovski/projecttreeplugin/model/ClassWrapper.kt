package com.github.maxstepanovski.projecttreeplugin.model

data class ClassWrapper(
        val name: String,
        val constructorParameters: List<ValueParameter>,
        val fields: List<ValueParameter>,
        val methods: List<FunctionWrapper>,
) {
    private val _dependencies: MutableList<ClassWrapper> = mutableListOf()
    val dependencies: List<ClassWrapper> = _dependencies

    fun addDependency(dependency: ClassWrapper) {
        _dependencies.add(dependency)
    }
}
