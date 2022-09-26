package com.github.maxstepanovski.projecttreeplugin.ui

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class DiagramEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.name.contains(FILE_NAME_POSTFIX)
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor = DiagramEditor(project, file)

    override fun getEditorTypeId(): String = EDITOR_TYPE_ID

    override fun getPolicy(): FileEditorPolicy = FileEditorPolicy.HIDE_DEFAULT_EDITOR

    companion object {
        const val FILE_NAME_POSTFIX = ".diagram"
        const val EDITOR_TYPE_ID = "teldrassil-editor"
    }
}