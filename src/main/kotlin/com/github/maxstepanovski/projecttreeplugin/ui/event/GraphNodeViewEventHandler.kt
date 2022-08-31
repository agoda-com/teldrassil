package com.github.maxstepanovski.projecttreeplugin.ui.event

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope

class GraphNodeViewEventHandler(private val classFullName: String): EventHandler {
    override fun doubleClicked(project: Project) {
        val psiFile =  getPsiClassByName(classFullName, project)
        psiFile?.navigate(true)
    }

    private fun getPsiClassByName(cls: String,project: Project): PsiClass? {
        val searchScope: GlobalSearchScope = GlobalSearchScope.allScope(project)
        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        return javaPsiFacade.findClass(cls, searchScope)
    }
}