package com.github.maxstepanovski.projecttreeplugin.parser

import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class KtClassParserTest : BasePlatformTestCase() {

    fun testKotlinClassParsed() {
        // given
        val ktClassParser = KtClassParser()
        val kotlinClassFile = myFixture.configureByFile("KotlinClass.kt")
        val kotlinClass = assertInstanceOf(kotlinClassFile, KtFile::class.java)

        // when
        kotlinClass.accept(ktClassParser)

        // then
        with(ktClassParser.getParsingResult()) {
            assert(name == "KotlinClass")
            assert(constructorParameters.size == 2)
            assert(fields.size == 1)
            assert(methods.size == 2)

            constructorParameters.let {
                assert(it.size == 2)
                assert(it[0].modifiers[0] == "private")
                assert(it[0].identifier == "first")
                assert(it[0].type == "String")
                assert(it[0].fullName == "kotlin.String")

                assert(it[1].modifiers[0] == "")
                assert(it[1].identifier == "second")
                assert(it[1].type == "Int")
                assert(it[1].fullName == "kotlin.Int")
            }

            fields.let {
                assert(it.size == 1)
                assert(it[0].modifiers[0] == "")
                assert(it[0].identifier == "third")
                assert(it[0].type == "Long")
                assert(it[0].fullName == "null")
            }

            methods.let {
                assert(it.size == 2)
                assert(it[0].modifiers[0] == "")
                assert(it[0].identifier == "publicFun")
                assert(it[0].arguments.size == 1)
                assert(it[0].arguments[0].modifiers[0] == "")
                assert(it[0].arguments[0].identifier == "argOne: Double")
                assert(it[0].arguments[0].type == "Double")
                assert(it[0].arguments[0].fullName == "kotlin.Double")
            }
        }
    }

    override fun getTestDataPath() = "src/test/testData/classes"
}