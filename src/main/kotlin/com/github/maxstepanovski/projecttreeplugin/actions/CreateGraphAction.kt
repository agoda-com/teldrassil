package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphNodeViewMapper
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.GraphHolder
import com.github.maxstepanovski.projecttreeplugin.parser.KtClassParser
import com.github.maxstepanovski.projecttreeplugin.ui.DiagramEditorProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtClass
import java.io.File
import java.nio.file.Path

class CreateGraphAction : AnAction() {
    private val ktClassParser = KtClassParser()
    private val mapper = ClassWrapperToGraphNodeViewMapper()

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
        ktClassParser.clearParsingResult()
        deque.addLast(rootNode)


        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            (node.constructorParameters + node.fields).forEach {
                psiFacade.findClass(it.fullName, GlobalSearchScope.projectScope(project))?.let { psiClass ->
                    val vfs = psiClass.containingFile.virtualFile
                    val psi = PsiManager.getInstance(project).findFile(vfs)
                    psi?.accept(ktClassParser)
                    ktClassParser.getParsingResult().let { childNode ->
                        node.addDependency(childNode)
                        deque.addLast(childNode)
                    }
                    ktClassParser.clearParsingResult()
                }
            }
        }

        val graphNodeViews = mapper.map(rootNode)
        val fileName = "${rootNode.name}${DiagramEditorProvider.FILE_NAME_POSTFIX}"
        GraphHolder.graphNodeViews[fileName] = graphNodeViews

        val filePath = "${project.basePath}/$fileName"
        if (File(filePath).createNewFile()) {
            val path = Path.of(filePath)
            val diagramVfs = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(path)
            if (diagramVfs != null) {
                FileEditorManager.getInstance(project).openFile(diagramVfs, false)
            }
        }
    }
}