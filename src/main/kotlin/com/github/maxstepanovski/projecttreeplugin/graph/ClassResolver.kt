package com.github.maxstepanovski.projecttreeplugin.graph

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper

interface ClassResolver {

    /**
     * resolves class by short name, only works if class is currently opened in the editor tab
     */
    fun resolveClassByName(name: String): ClassWrapper?

    /**
     * resolves class by full name, independent of editor state
     */
    fun resolveClassByFullName(fullName: String): ClassWrapper?
}