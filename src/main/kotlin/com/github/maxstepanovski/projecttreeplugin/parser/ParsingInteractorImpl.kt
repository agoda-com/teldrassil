package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

class ParsingInteractorImpl : ParsingInteractor {
    private val ktClassParser = KtClassParser()
    private val javaClassParser = JavaClassParser()

    override fun parseFile(file: PsiFile?, className: String): ClassWrapper? {
        // class declaration can't contain '?', whereas type can be nullable
        val nonNullableClassName = className.replace("?", "")
        return when (file) {
            is KtFile -> {
                file.children
                        // kotlin object is not a KtClass from psi perspective, but a KtObjectDeclaration
                        // need to parse separately
                        .filterIsInstance<KtClassOrObject>()
                        .find { it.name == nonNullableClassName }
                        ?.let {
                            it.accept(ktClassParser)
                            val result = ktClassParser.getParsingResult()
                            ktClassParser.clearParsingResult()
                            result
                        }
            }
            is PsiJavaFile -> {
                file.children
                        .filterIsInstance<PsiClass>()
                        .find { it.name == nonNullableClassName }
                        ?.let {
                            it.accept(javaClassParser)
                            val result = javaClassParser.getParsingResult()
                            javaClassParser.clearParsingResult()
                            result
                        }
            }
            else -> null
        }
    }
}