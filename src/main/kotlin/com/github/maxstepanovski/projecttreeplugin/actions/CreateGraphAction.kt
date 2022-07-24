package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.parser.KtClassParser
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class CreateGraphAction : AnAction() {
    private val ktClassParser = KtClassParser()

    override fun actionPerformed(e: AnActionEvent) {
        val deque = ArrayDeque<ClassWrapper>()

        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val psiFacade = JavaPsiFacade.getInstance(project)


        val clickedPsiElement = file.findElementAt(editor.caretModel.offset) ?: return
        val clickedKtClass = file.children
                .filterIsInstance<KtClass>()
                .find {
                    clickedPsiElement.text == it.name
                }
                ?: return

        clickedKtClass.accept(ktClassParser)
        val rootNode = ktClassParser.getParsingResult()
        deque.addLast(rootNode)

        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            node.constructorParameters.forEach {
                psiFacade.findClass(it.fullName, GlobalSearchScope.projectScope(project))?.let { psiClass ->
                    KtPsiFactory(project).createClass(psiClass.text).accept(ktClassParser)
                    ktClassParser.getParsingResult().let { childNode ->
                        node.dependencies.add(childNode)
                        deque.addLast(childNode)
                    }
                }
            }
        }

        println(rootNode)
    }
}