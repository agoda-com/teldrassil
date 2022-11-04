package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.contract.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.FunctionWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import com.intellij.psi.PsiClass
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.idea.util.isAnonymousFunction
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.util.*

class UastClassParser : AbstractUastVisitor() {
    private var name = ""
    private var fullClassName = ""
    private var type = ClassType.CLASS
    private val fields = mutableListOf<ValueParameter>()
    private val methods = mutableListOf<FunctionWrapper>()
    private val directInheritors = mutableListOf<ValueParameter>()

    override fun visitClass(node: UClass): Boolean {
        if (name.isNotEmpty()) {
            // TODO support inner classes
            return false
        }
        name = node.name.orEmpty()
        fullClassName = node.qualifiedName.orEmpty()
        type = node.extractType()

        node.fields.forEach {
            fields.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier.text.orEmpty(),
                    it.type.presentableText,
                    it.type.canonicalText
            ))
        }

        node.getAsJavaPsiElement(PsiClass::class.java)?.let { psiClass ->
            DirectClassInheritorsSearch.search(psiClass).findAll().forEach {
                if (it.isAnonymousFunction.not()) {
                    val packageName = PsiUtil.getPackageName(it).orEmpty()
                    val name = PsiUtil.getName(it) ?: "anonymous class"
                    directInheritors.add(ValueParameter(
                            emptyList(),
                            "",
                            name,
                            "$packageName.$name"
                    ))
                }
            }
        }

        return false
    }

    override fun visitMethod(uMethod: UMethod): Boolean {
        val modifiers = uMethod.modifierList.text.orEmpty()
        val identifier = uMethod.nameIdentifier?.text.orEmpty()
        val arguments = uMethod.parameterList.parameters.fold(mutableListOf<ValueParameter>()) { init, param ->
            init.add(ValueParameter(
                    modifiers = listOf(param.modifierList?.text.orEmpty()),
                    identifier = param.name,
                    type = param.type.presentableText,
                    fullName = param.type.canonicalText
            ))
            init
        }
        val returnType = uMethod.returnType?.presentableText.orEmpty()

        methods.add(FunctionWrapper(
                listOf(modifiers),
                identifier,
                arguments,
                returnType
        ))
        return true
    }

    override fun visitParameter(node: UParameter): Boolean {
        return super.visitParameter(node)
    }

    override fun visitField(node: UField): Boolean {
        return super.visitField(node)
    }

    fun getParsingResult(): ClassWrapper = ClassWrapper(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            constructorParameters = emptyList(),
            fields = fields.toList(),
            methods = methods.toList(),
            directInheritors = directInheritors.toList(),
            fullClassName
    )

    fun clearParsingResult() {
        name = ""
        fields.clear()
        methods.clear()
        directInheritors.clear()
    }

    private fun PsiClass.extractType(): ClassType {
        return when {
            isEnum -> ClassType.ENUM
            isInterface -> ClassType.INTERFACE
            else -> ClassType.CLASS
        }
    }
}