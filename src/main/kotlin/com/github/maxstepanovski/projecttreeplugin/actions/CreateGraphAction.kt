package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.GraphHolder
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractor
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractorImpl
import com.github.maxstepanovski.projecttreeplugin.ui.DiagramEditorProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import java.io.File
import java.nio.file.Path

class CreateGraphAction : AnAction() {
    private val parsingInteractor: ParsingInteractor = ParsingInteractorImpl()
    private val mapper = ClassWrapperToGraphViewMapper()

    override fun actionPerformed(e: AnActionEvent) {
        // initialization
        val file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val psiFacade = JavaPsiFacade.getInstance(project)
        val psiManager = PsiManager.getInstance(project)
        val vfManager = VirtualFileManager.getInstance()

        // get name of the element with caret(cursor) on it.
        // caret should be positioned on class name,
        // otherwise unclear which class in the file should be the root (if more than 1 class in file)
        val className: String = file.findElementAt(editor.caretModel.offset)?.text ?: return

        // parse class, given its name and parent file
        // this class will represent the root node
        val rootNode: ClassWrapper = parsingInteractor.parseFile(file, className) ?: return
        val deque = ArrayDeque<ClassWrapper>()
        deque.addLast(rootNode)

        // bfs traverse all class dependencies, attaching dependencies to nodes along the way
        while (deque.isNotEmpty()) {
            val node = deque.removeFirst()
            (node.constructorParameters + node.fields).forEach {
                psiFacade.findClass(it.fullName, GlobalSearchScope.projectScope(project))?.let { psiClass ->
                    val psi = psiManager.findFile(psiClass.containingFile.virtualFile)
                    parsingInteractor.parseFile(psi, it.type)?.let { childNode ->
                        node.addDependency(childNode)
                        deque.addLast(childNode)
                    }
                }
            }
        }

        // map graph to its presentation model
        val graphView = mapper.map(rootNode)

        val fileName = "${rootNode.name}${DiagramEditorProvider.FILE_NAME_POSTFIX}"
        GraphHolder.graphViews[fileName] = graphView

        // create a file in project root with .diagram extension,
        // refresh project tree and open file
        val filePath = "${project.basePath}/$fileName"
        if (File(filePath).createNewFile()) {
            vfManager.refreshAndFindFileByNioPath(Path.of(filePath))?.let {
                FileEditorManager.getInstance(project).openFile(it, false)
            }
        }
    }
}