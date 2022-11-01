package com.github.maxstepanovski.projecttreeplugin.parser

import com.github.maxstepanovski.projecttreeplugin.model.ClassType
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.uast.toUElement

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class UastClassParserTest : BasePlatformTestCase() {

    fun testKotlinInterfaceParsed() {
        // given
        val uastClassParser = UastClassParser()
        val files = myFixture.configureByFiles("KotlinInterface.kt", "KotlinClass.kt")
        val ktInterface = files[0]
        val kotlinInterface = assertInstanceOf(ktInterface, KtFile::class.java)

        // when
        kotlinInterface.toUElement()?.accept(uastClassParser)

        // then
        with(uastClassParser.getParsingResult()) {
            assertEquals(name, "KotlinInterface")
            assertEquals(type, ClassType.INTERFACE)
            assertEquals(fullClassName, "com.github.maxstepanovski.projecttreeplugin.KotlinInterface")
            assertTrue(directInheritors.size == 1)
            assertTrue(directInheritors[0].fullName == "com.github.maxstepanovski.projecttreeplugin.KotlinClass")

            assertEquals(methods.size, 3)
            methods[0].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "firstFunction")
                assertEquals(it.arguments.size, 0)
                assertEquals(it.returnType, "void")
            }
            methods[1].let {
                assertEquals(it.modifiers[0], "protected")
                assertEquals(it.identifier, "secondFunction")
                assertEquals(it.arguments.size, 2)
                it.arguments[0].let { arg ->
                    assertEquals(arg.modifiers[0], "")
                    assertEquals(arg.identifier, "arg1")
                    assertEquals(arg.type, "int")
                    assertEquals(arg.fullName, "int")
                }
                it.arguments[1].let { arg ->
                    assertEquals(arg.modifiers[0], "")
                    assertEquals(arg.identifier, "arg2")
                    assertEquals(arg.type, "String")
                    assertEquals(arg.fullName, "java.lang.String")
                }
                assertEquals(it.returnType, "long")
            }
            methods[2].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "value")
                assertEquals(it.returnType, "KotlinClass")
            }
        }
    }

    fun testKotlinClassParsed() {
        // given
        val uastClassParser = UastClassParser()
        val files = myFixture.configureByFiles("KotlinClass.kt")
        val kotlinClass = assertInstanceOf(files[0], KtFile::class.java)

        // when
        kotlinClass.toUElement()?.accept(uastClassParser)

        // then
        with(uastClassParser.getParsingResult()) {
            assertEquals(name, "KotlinClass")
            assertEquals(type, ClassType.CLASS)
            assertEquals(fullClassName, "com.github.maxstepanovski.projecttreeplugin.KotlinClass")
            assertEquals(directInheritors.size, 0)

            assertEquals(fields.size, 3)

            fields[0].let {
                assertEquals(it.modifiers[0], "private")
                assertEquals(it.identifier, "first")
                assertEquals(it.type, "String")
                assertEquals(it.fullName, "java.lang.String")
            }
            fields[1].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "second")
                assertEquals(it.type, "int")
                assertEquals(it.fullName, "int")
            }
            fields[2].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "third")
                assertEquals(it.type, "long")
                assertEquals(it.fullName, "long")
            }

            assertEquals(methods.size, 5)

            // implicit field getter
            methods[0].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "third")
                assertEquals(it.arguments.size, 0)
                assertEquals(it.returnType, "long")
            }

            methods[1].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "publicFun")
                assertEquals(it.arguments[0].modifiers[0], "")
                assertEquals(it.arguments[0].identifier, "argOne")
                assertEquals(it.arguments[0].type, "double")
                assertEquals(it.arguments[0].fullName, "double")
                assertEquals(it.returnType, "void")
            }

            methods[2].let {
                assertEquals(it.modifiers[0], "private")
                assertEquals(it.identifier, "privateFun")
                assertEquals(it.arguments[0].modifiers[0], "")
                assertEquals(it.arguments[0].identifier, "argOne")
                assertEquals(it.arguments[0].type, "String")
                assertEquals(it.arguments[0].fullName, "java.lang.String")
                assertEquals(it.arguments[1].modifiers[0], "")
                assertEquals(it.arguments[1].identifier, "argTwo")
                assertEquals(it.arguments[1].type, "Function0<Unit>")
                assertEquals(it.arguments[1].fullName, "kotlin.jvm.functions.Function0<kotlin.Unit>")
                assertEquals(it.returnType, "boolean")
            }

            // implicit field getter
            methods[3].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "second")
                assertEquals(it.arguments.size, 0)
                assertEquals(it.returnType, "int")
            }

            // constructor
            methods[4].let {
                assertEquals(it.modifiers[0], "")
                assertEquals(it.identifier, "KotlinClass")
                assertEquals(it.arguments[0].modifiers[0], "private")
                assertEquals(it.arguments[0].identifier, "first")
                assertEquals(it.arguments[0].type, "String")
                assertEquals(it.arguments[0].fullName, "java.lang.String")
                assertEquals(it.arguments[1].modifiers[0], "")
                assertEquals(it.arguments[1].identifier, "second")
                assertEquals(it.arguments[1].type, "int")
                assertEquals(it.arguments[1].fullName, "int")
                assertEquals(it.returnType, "")
            }
        }
    }

    fun testJavaClassParsed() {
        // given
        val uastClassParser = UastClassParser()
        val files = myFixture.configureByFiles("JavaClass.java")
        val javaClass = assertInstanceOf(files[0], PsiJavaFile::class.java)

        // when
        javaClass.toUElement()?.accept(uastClassParser)

        // then
        with(uastClassParser.getParsingResult()) {
            assertEquals(name, "JavaClass")
            assertEquals(fullClassName, "com.github.maxstepanovski.projecttreeplugin.JavaClass")
            assertEquals(type, ClassType.CLASS)

            fields[0].let {
                assertEquals(it.modifiers[0], "public")
                assertEquals(it.identifier, "string")
                assertEquals(it.type, "String")
                assertEquals(it.fullName, "String")
            }

            fields[1].let {
                assertEquals(it.modifiers[0], "private")
                assertEquals(it.identifier, "number")
                assertEquals(it.type, "int")
                assertEquals(it.fullName, "int")
            }

            fields[2].let {
                assertEquals(it.modifiers[0], "private")
                assertEquals(it.identifier, "doubleNumber")
                assertEquals(it.type, "double")
                assertEquals(it.fullName, "double")
            }

            // first constructor
            methods[0].let {
                assertEquals(it.modifiers[0], "public")
                assertEquals(it.identifier, "JavaClass")
                assertEquals(it.arguments[0].modifiers[0], "")
                assertEquals(it.arguments[0].identifier, "number")
                assertEquals(it.arguments[0].type, "int")
                assertEquals(it.arguments[0].fullName, "int")
                assertEquals(it.arguments[1].modifiers[0], "")
                assertEquals(it.arguments[1].identifier, "doubleNumber")
                assertEquals(it.arguments[1].type, "double")
                assertEquals(it.arguments[1].fullName, "double")
                assertEquals(it.returnType, "")
            }

            // second constructor
            methods[1].let {
                assertEquals(it.modifiers[0], "public")
                assertEquals(it.identifier, "JavaClass")
                assertEquals(it.returnType, "")
            }

            methods[2].let {
                assertEquals(it.modifiers[0], "public")
                assertEquals(it.identifier, "foo")
                assertEquals(it.arguments[0].modifiers[0], "")
                assertEquals(it.arguments[0].identifier, "string")
                assertEquals(it.arguments[0].type, "String")
                assertEquals(it.arguments[0].fullName, "String")
                assertEquals(it.arguments[1].modifiers[0], "")
                assertEquals(it.arguments[1].identifier, "integer")
                assertEquals(it.arguments[1].type, "Integer")
                assertEquals(it.arguments[1].fullName, "Integer")
                assertEquals(it.returnType, "String")
            }
        }
    }

    override fun getTestDataPath() = "src/test/testData/classes"
}