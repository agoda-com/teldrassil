package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.FunctionWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.searches.DirectClassInheritorsSearch
import com.intellij.psi.util.PsiUtil
import java.util.*

class JavaClassParser : JavaRecursiveElementVisitor() {
    private var name = ""
    private var type = ClassType.CLASS
    private val constructorParameters = mutableListOf<ValueParameter>()
    private val fields = mutableListOf<ValueParameter>()
    private val methods = mutableListOf<FunctionWrapper>()
    private val directInheritors = mutableListOf<ValueParameter>()

    override fun visitMethod(method: PsiMethod) {
        super.visitMethod(method)

        val modifiers = method.modifierList.text.orEmpty()
        val identifier = method.nameIdentifier?.text.orEmpty()
        val arguments = method.parameterList.parameters.fold(mutableListOf<ValueParameter>()) { init, param ->
            init.add(ValueParameter(
                    modifiers = listOf(param.modifierList?.text.orEmpty()),
                    identifier = param.text,
                    type = param.type.presentableText,
                    fullName = param.type.canonicalText
            ))
            init
        }
        val returnType = method.returnType?.presentableText.orEmpty()

        methods.add(FunctionWrapper(
                listOf(modifiers),
                identifier,
                arguments,
                returnType
        ))
    }

    override fun visitClass(aClass: PsiClass) {
        super.visitClass(aClass)

        name = aClass.name.orEmpty()
        type = aClass.extractType()

        DirectClassInheritorsSearch.search(aClass).findAll().forEach {
            val packageName = PsiUtil.getPackageName(it)
            val name = PsiUtil.getName(it)
            directInheritors.add(ValueParameter(
                    emptyList(),
                    "",
                    name,
                    "$packageName.$name"
            ))
        }

        aClass.fields.forEach {
            fields.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier.text.orEmpty(),
                    it.type.presentableText,
                    it.type.canonicalText
            ))
        }
    }

    fun getParsingResult(): ClassWrapper = ClassWrapper(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            constructorParameters = constructorParameters.toList(),
            fields = fields.toList(),
            methods = methods.toList(),
            directInheritors = directInheritors.toList()
    )

    fun clearParsingResult() {
        name = ""
        constructorParameters.clear()
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