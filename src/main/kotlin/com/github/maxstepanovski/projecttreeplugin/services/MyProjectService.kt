package com.github.maxstepanovski.projecttreeplugin.services

import com.intellij.openapi.project.Project
import com.github.maxstepanovski.projecttreeplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
