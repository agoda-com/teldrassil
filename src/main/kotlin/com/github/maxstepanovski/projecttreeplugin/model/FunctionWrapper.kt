package com.github.maxstepanovski.projecttreeplugin.model

data class FunctionWrapper(
        val modifiers: List<String>,
        val identifier: String,
        val arguments: List<ValueParameter>,
        val returnType: String
)
