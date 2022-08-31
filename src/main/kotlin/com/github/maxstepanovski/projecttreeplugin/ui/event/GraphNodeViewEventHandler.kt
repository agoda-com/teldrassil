package com.github.maxstepanovski.projecttreeplugin.ui.event

import com.github.maxstepanovski.projecttreeplugin.dependency.DependencyManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope

class GraphNodeViewEventHandler(private val classFullName: String): EventHandler {
    override fun doubleClicked() {
        val psiFile =  getPsiClassByName(classFullName)
        psiFile?.navigate(true)
    }

    private fun getPsiClassByName(cls: String): PsiClass? {
        val searchScope: GlobalSearchScope = GlobalSearchScope.allScope(DependencyManager.project)
        val javaPsiFacade = JavaPsiFacade.getInstance(DependencyManager.project)
        return javaPsiFacade.findClass(cls, searchScope)
    }
}