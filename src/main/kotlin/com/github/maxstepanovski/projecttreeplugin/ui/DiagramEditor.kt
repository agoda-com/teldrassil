package com.github.maxstepanovski.projecttreeplugin.ui

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent


class DiagramEditor(
        private val project: Project,
        private val virtualFile: VirtualFile
) : FileEditor {

    override fun getFile(): VirtualFile? = virtualFile

    override fun <T : Any?> getUserData(key: Key<T>): T? = null

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
    }

    override fun dispose() {
        Disposer.dispose(this)
    }

    override fun getComponent(): JComponent = ZoomablePanel()

    override fun getPreferredFocusedComponent(): JComponent? = null

    override fun getName(): String = virtualFile.name

    override fun setState(state: FileEditorState) {
    }

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getCurrentLocation(): FileEditorLocation? = null
}