package com.github.maxstepanovski.projecttreeplugin.services

import com.intellij.openapi.project.Project
import com.github.maxstepanovski.projecttreeplugin.MyBundle

class ProjectTreeProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService"))
    }
}
