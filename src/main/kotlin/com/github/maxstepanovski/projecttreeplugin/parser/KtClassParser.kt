package com.github.maxstepanovski.projecttreeplugin.parser

import com.android.tools.idea.kotlin.getQualifiedName
import com.github.maxstepanovski.projecttreeplugin.getFullName
import com.github.maxstepanovski.projecttreeplugin.model.ClassType
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.FunctionWrapper
import com.github.maxstepanovski.projecttreeplugin.model.ValueParameter
import org.jetbrains.kotlin.idea.search.getKotlinFqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import java.util.*

class KtClassParser : KtTreeVisitorVoid() {
    private var name = ""
    private var fullClassName = ""
    private var type = ClassType.CLASS
    private val constructorParameters = mutableListOf<ValueParameter>()
    private val fields = mutableListOf<ValueParameter>()
    private val methods = mutableListOf<FunctionWrapper>()
    private val directInheritors = mutableListOf<ValueParameter>()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        type = klass.extractType()
        name = klass.name.orEmpty()
        fullClassName = klass.getQualifiedName().orEmpty()
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
                    it.containingFile.getKotlinFqName().toString()
            ))
        }
    }

    fun getParsingResult(): ClassWrapper = ClassWrapper(
            // copy lists, so that cal to `clearParsingResult` doesn't delete all references
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            constructorParameters = constructorParameters.toList(),
            fields = fields.toList(),
            methods = methods.toList(),
            directInheritors = directInheritors.toList(),
            fullClassName
    )

    fun clearParsingResult() {
        name = ""
        constructorParameters.clear()
        fields.clear()
        methods.clear()
        directInheritors.clear()
    }

    private fun KtClass.extractType(): ClassType {
        return when {
            isInterface() -> ClassType.INTERFACE
            isData() -> ClassType.DATA_CLASS
            isEnum() -> ClassType.ENUM
            else -> ClassType.CLASS
        }
    }
}