package com.github.maxstepanovski.projecttreeplugin.actions

import com.github.maxstepanovski.projecttreeplugin.data.repository.DiagramRepository
import com.github.maxstepanovski.projecttreeplugin.graph.BfsGraphBuilder
import com.github.maxstepanovski.projecttreeplugin.graph.ClassResolver
import com.github.maxstepanovski.projecttreeplugin.graph.GraphBuilder
import com.github.maxstepanovski.projecttreeplugin.model.ClassWrapper
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractor
import com.github.maxstepanovski.projecttreeplugin.parser.ParsingInteractorImpl
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

class CreateGraphAction : AnAction(), ClassResolver {
    private val graphBuilder: GraphBuilder = BfsGraphBuilder(this)
    private val parsingInteractor: ParsingInteractor = ParsingInteractorImpl()

    private lateinit var file: PsiFile
    private lateinit var project: Project
    private lateinit var psiFacade: JavaPsiFacade
    private lateinit var psiManager: PsiManager
    private lateinit var globalSearchScope: GlobalSearchScope
    private lateinit var vfManager: VirtualFileManager
    private lateinit var editor: Editor
    private lateinit var diagramRepository: DiagramRepository

    override fun actionPerformed(e: AnActionEvent) {
        // initialization
        file = e.getData(CommonDataKeys.PSI_FILE) ?: return
        project = e.project ?: return
        psiFacade = JavaPsiFacade.getInstance(project)
        psiManager = PsiManager.getInstance(project)
        globalSearchScope = GlobalSearchScope.projectScope(project)
        editor = e.getData(CommonDataKeys.EDITOR) ?: return
        vfManager = VirtualFileManager.getInstance()
        diagramRepository = DiagramRepository(project)

        // get name of the element with caret(cursor) on it.
        // caret should be positioned on class name,
        // otherwise unclear which class in the file should be the root (if more than 1 class in file)
        val shortName: String = file.findElementAt(editor.caretModel.offset)?.text ?: return
        val packageName = when (file) {
            is PsiJavaFile -> (file as PsiJavaFile).packageName
            is KtFile -> (file as KtFile).packageName
            else -> ""
        }
        val fullName = "${packageName}.${shortName}"

        // if file doesn't exist, create class dependencies graph and save it to the file
        if (diagramRepository.shouldCreateFile(fullName)) {
            val rootNode: ClassWrapper = graphBuilder.buildGraph(fullName) ?: return
            diagramRepository.saveToFile(rootNode)
        }

        // open an editor tab for given file
        vfManager.refreshAndFindFileByNioPath(Path.of(diagramRepository.getFilePath(shortName)))?.let {
            FileEditorManager.getInstance(project).openFile(it, false)
        }
    }

    override fun resolveClassByFullName(fullName: String): ClassWrapper? =
            psiFacade.findClass(fullName, globalSearchScope)
                ?.let {
                    psiManager.findFile(it.containingFile.virtualFile)
                }
                ?.let {
                    parsingInteractor.parseFile(it, fullName.split(".").last())
                }
}