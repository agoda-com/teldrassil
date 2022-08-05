package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.intellij.psi.PsiFile

interface ParsingInteractor {

    /**
     * Parses kt/java class by name from the given file
     */
    fun parseFile(file: PsiFile?, className: String): ClassWrapper?
}