package com.github.maxstepanovski.projecttreeplugin.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.maxstepanovski.projecttreeplugin.services.*

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<ProjectTreeProjectService>()
    }
}
