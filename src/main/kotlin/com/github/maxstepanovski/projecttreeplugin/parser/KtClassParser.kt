package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.getFullName
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.FunctionWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class KtClassParser : KtTreeVisitorVoid() {
    private var name = ""
    private val constructorParameters = mutableListOf<ValueParameter>()
    private val fields = mutableListOf<ValueParameter>()
    private val methods = mutableListOf<FunctionWrapper>()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        name = klass.name.orEmpty()
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        constructor.valueParameters.forEach {
            constructorParameters.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier?.text.orEmpty(),
                    it.typeReference?.text.orEmpty(),
                    it.getFullName()
            ))
        }
    }

    override fun visitClassBody(classBody: KtClassBody) {
        super.visitClassBody(classBody)

        classBody.functions.forEach {
            val modifiers = it.modifierList?.text.orEmpty()
            val identifier = it.nameIdentifier?.text.orEmpty()
            val arguments = it.valueParameters.fold(mutableListOf<ValueParameter>()) { init, param ->
                init.add(ValueParameter(
                        modifiers = listOf(param.modifierList?.text.orEmpty()),
                        identifier = param.text,
                        type = param.typeReference?.text.orEmpty(),
                        fullName = param.getFullName()
                ))
                init
            }
            val returnType = it.typeReference?.text.orEmpty()

            methods.add(FunctionWrapper(
                    listOf(modifiers),
                    identifier,
                    arguments,
                    returnType
            ))
        }

        classBody.properties.forEach {
            fields.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier?.text.orEmpty(),
                    it.typeReference?.text.orEmpty(),
                    it.getFullName()
            ))
        }
    }

    fun getParsingResult(): ClassWrapper = ClassWrapper(
            name,
            constructorParameters.toList(),
            fields.toList(),
            methods.toList()
    )

    fun clearParsingResult() {
        name = ""
        constructorParameters.clear()
        fields.clear()
        methods.clear()
    }
}