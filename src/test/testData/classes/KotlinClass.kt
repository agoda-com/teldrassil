package com.github.maxstepanovski.projecttreeplugin
class KotlinClass(
        private val first: String,
        val second: Int
): KotlinInterface {
    val third: Long = 0

    fun publicFun(argOne: Double) {
    }

    private fun privateFun(argOne: String, argTwo: () -> Unit): Boolean = true
}