package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

interface ClassResolver {

    /**
     * resolves class by full name, independent of editor state
     */
    fun resolveClassByFullName(fullName: String): ClassWrapper?
}