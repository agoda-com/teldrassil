package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.FunctionWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

class KtClassParser : KtTreeVisitorVoid() {
    private var classWrapper = ClassWrapper()

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        constructor.valueParameters.forEach {
            classWrapper.constructorParameters.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier?.text.orEmpty(),
                    it.typeReference?.text.orEmpty()
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
                        type = param.typeReference?.text.orEmpty()
                ))
                init
            }
            val returnType = it.typeReference?.text.orEmpty()

            classWrapper.methods.add(FunctionWrapper(
                    listOf(modifiers),
                    identifier,
                    arguments,
                    returnType
            ))
        }

        classBody.properties.forEach {
            classWrapper.fields.add(ValueParameter(
                    listOf(it.modifierList?.text.orEmpty()),
                    it.nameIdentifier?.text.orEmpty(),
                    it.typeReference?.text.orEmpty()
            ))
        }
    }

    fun clearParsingResult() {
        classWrapper = ClassWrapper()
    }

    fun getParsingResult(): ClassWrapper = classWrapper
}