package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.Color
import java.awt.Graphics
import java.awt.event.*
import javax.swing.JButton
import javax.swing.JPanel


class ZoomablePanel : JPanel(), MouseWheelListener, MouseListener, MouseMotionListener {
    private val button = JButton("someButton")

    init {
        addMouseWheelListener(this)
        addMouseMotionListener(this)
        addMouseListener(this)
        setSize(2000, 2000)
        background = Color.WHITE
        add(button)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
    }

    override fun mouseWheelMoved(e: MouseWheelEvent) {
    }

    override fun mouseDragged(e: MouseEvent) {
    }

    override fun mouseMoved(e: MouseEvent) {
    }

    override fun mouseClicked(e: MouseEvent) {
    }

    override fun mousePressed(e: MouseEvent) {
    }

    override fun mouseReleased(e: MouseEvent) {
    }

    override fun mouseEntered(e: MouseEvent) {
    }

    override fun mouseExited(e: MouseEvent) {
    }
}