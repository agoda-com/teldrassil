package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.graph.ClassResolver
import com.github.maxstepanovski.projecttreeplugin.graph.GraphBuilder
import com.github.maxstepanovski.projecttreeplugin.graph.StandardGraphBuilder
import com.github.maxstepanovski.projecttreeplugin.mapper.ClassWrapperToGraphViewMapper
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.model.GraphHolder
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractor
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractorImpl
import com.github.maxstepanovski.projecttreeplugin.ui.DiagramEditorProvider
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import java.io.File
import java.nio.file.Path

class CreateGraphAction : AnAction(), ClassResolver {
    private val graphBuilder: GraphBuilder = StandardGraphBuilder(this)
    private val parsingInteractor: ParsingInteractor = ParsingInteractorImpl()
    private val mapper = ClassWrapperToGraphViewMapper()

    private lateinit var file: PsiFile
    private lateinit var project: Project
    private lateinit var psiFacade: JavaPsiFacade
    private lateinit var psiManager: PsiManager
    private lateinit var globalSearchScope: GlobalSearchScope
    private lateinit var vfManager: VirtualFileManager
    private lateinit var editor: Editor

    override fun actionPerformed(e: AnActionEvent) {
        // initialization
        file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        project = e.project ?: return
        psiFacade = JavaPsiFacade.getInstance(project)
        psiManager = PsiManager.getInstance(project)
        globalSearchScope = GlobalSearchScope.projectScope(project)
        editor = e.getData(CommonDataKeys.EDITOR) ?: return
        vfManager = VirtualFileManager.getInstance()

        // get name of the element with caret(cursor) on it.
        // caret should be positioned on class name,
        // otherwise unclear which class in the file should be the root (if more than 1 class in file)
        val className: String = file.findElementAt(editor.caretModel.offset)?.text ?: return

        // create class dependencies graph
        val rootNode: ClassWrapper = graphBuilder.buildGraph(className) ?: return

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

    override fun resolveClassByName(name: String): ClassWrapper? =
            parsingInteractor.parseFile(file, name)

    override fun resolveClassByFullName(fullName: String): ClassWrapper? =
            psiFacade.findClass(fullName, globalSearchScope)
                    ?.let {
                        psiManager.findFile(it.containingFile.virtualFile)
                    }
                    ?.let {
                        parsingInteractor.parseFile(it, fullName.split(".").last())
                    }
}