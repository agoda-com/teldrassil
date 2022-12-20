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
import com.intellij.openapi.application.AccessToken
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import com.intellij.openapi.progress.runBackgroundableTask
import javax.swing.SwingUtilities


class CreateGraphAction : AnAction(), ClassResolver {
    private val graphBuilder: GraphBuilder = BfsGraphBuilder(this)
    private val parsingInteractor: ParsingInteractor = ParsingInteractorImpl()

    private lateinit var file: PsiFile
    private lateinit var psiFacade: JavaPsiFacade
    private lateinit var psiManager: PsiManager
    private lateinit var globalSearchScope: GlobalSearchScope
    private lateinit var vfManager: VirtualFileManager
    private lateinit var editor: Editor
    private lateinit var diagramRepository: DiagramRepository

    override fun actionPerformed(e: AnActionEvent) {
        runBackgroundableTask("Generating Dependency Graph", e.project, true) {
            val indicator = it
            val project = e.project ?: return@runBackgroundableTask

            // initialization
            file = e.getData(CommonDataKeys.PSI_FILE) ?: return@runBackgroundableTask
            psiFacade = JavaPsiFacade.getInstance(project)
            psiManager = PsiManager.getInstance(project)
            globalSearchScope = GlobalSearchScope.projectScope(project)
            editor = e.getData(CommonDataKeys.EDITOR) ?: return@runBackgroundableTask
            vfManager = VirtualFileManager.getInstance()
            diagramRepository = DiagramRepository(project)

            // get name of the element with caret(cursor) on it.
            // caret should be positioned on class name,
            // otherwise unclear which class in the file should be the root (if more than 1 class in file)
            val shortName: String = file.findElementAt(editor.caretModel.offset)?.text ?: return@runBackgroundableTask
            val packageName = when (file) {
                is PsiJavaFile -> (file as PsiJavaFile).packageName
                is KtFile -> (file as KtFile).packageName
                else -> ""
            }
            val fullName = "${packageName}.${shortName}"

            // if file doesn't exist, create class dependencies graph and save it to the file
            try {
                ApplicationManager.getApplication().runReadAction {
                    if (diagramRepository.shouldCreateFile(fullName)) {
                        indicator.text = "Creating dependency graph and saving to file"
                        val rootNode: ClassWrapper = graphBuilder.buildGraph(fullName) ?: return@runReadAction
                        diagramRepository.saveToFile(rootNode)
                    }


                    // open an editor tab for given file
                }
            } catch (ex: Exception) {
                indicator.cancel()
                ex.printStackTrace()
            }
            indicator.text = "Opening graph in editor"

            vfManager.refreshAndFindFileByNioPath(Path.of(diagramRepository.getFilePath(shortName)))?.let {
                SwingUtilities.invokeLater {
                    FileEditorManager.getInstance(project).openFile(it, false)
                }
            }
        }
    }

    override fun resolveClassByFullName(fullName: String): ClassWrapper? =
        psiFacade.findClass(fullName, globalSearchScope)?.let {
                psiManager.findFile(it.containingFile.virtualFile)
            }?.let {
                parsingInteractor.parseFile(it, fullName.split(".").last())
            }
}