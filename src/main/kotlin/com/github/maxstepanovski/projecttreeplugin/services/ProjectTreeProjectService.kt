package com.github.maxstepanovski.projecttreeplugin.services

import com.intellij.openapi.project.Project
import com.github.maxstepanovski.projecttreeplugin.ProjectTreeBundle

class ProjectTreeProjectService(project: Project) {

    init {
        println(ProjectTreeBundle.message("projectService"))
    }
}
