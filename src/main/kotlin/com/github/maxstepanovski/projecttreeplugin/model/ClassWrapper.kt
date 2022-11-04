package com.github.maxstepanovski.projecttreeplugin.model

import com.github.maxstepanovski.contract.model.ClassType

data class ClassWrapper(
    val id: String,
    val name: String,
    val type: ClassType = ClassType.CLASS,
    val constructorParameters: List<ValueParameter>,
    val fields: List<ValueParameter>,
    val methods: List<FunctionWrapper>,
    val directInheritors: List<ValueParameter>,
    val fullClassName: String
) {
    private val _dependencies: MutableList<ClassWrapper> = mutableListOf()
    val dependencies: List<ClassWrapper> = _dependencies

    fun addDependency(dependency: ClassWrapper) {
        _dependencies.add(dependency)
    }
}
