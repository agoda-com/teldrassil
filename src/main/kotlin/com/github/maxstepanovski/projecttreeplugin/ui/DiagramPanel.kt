package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.data.repository.DiagramRepository
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import javax.swing.JPanel

class DiagramPanel(val project: Project, virtualFile: VirtualFile) : JPanel(BorderLayout()) {
    private val diagramRepository = DiagramRepository(project)

    init {
        val graphPanel = GraphPanel(project, diagramRepository, virtualFile)
        val controlPanel = ControlPanel(
                setZoomCallback = {
                    if (it) {
                        graphPanel.zoomIndex += 1
                    } else {
                        graphPanel.zoomIndex -= 1
                    }
                },
                setFontCallback = {
                    if (it) {
                        graphPanel.increaseFontSize()
                    } else {
                        graphPanel.decreaseFontSize()
                    }
                },
                switchEdgeMode = {
                    graphPanel.switchEdgeMode()
                }
        )
        add(controlPanel, BorderLayout.NORTH)
        add(graphPanel, BorderLayout.CENTER)
    }
}