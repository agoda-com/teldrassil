package com.github.maxstepanovski.projecttreeplugin.ui.event

import com.intellij.openapi.project.Project

interface EventHandler {
    fun doubleClicked(project: Project)
}