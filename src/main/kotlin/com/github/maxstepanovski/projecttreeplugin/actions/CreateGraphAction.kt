package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.parser.KtClassParser
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.jetbrains.kotlin.psi.KtClass

class CreateGraphAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val clickedElement = file.findElementAt(editor.caretModel.offset) ?: return

        file.children.forEach {
            if (it is KtClass && clickedElement.text == it.name) {

                val ktClassParser = KtClassParser()

                file.accept(ktClassParser)

                val result = ktClassParser.getParsingResult()
            }
        }
    }
}