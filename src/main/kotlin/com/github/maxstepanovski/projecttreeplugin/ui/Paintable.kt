package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.Graphics2D

interface Paintable {

    fun size(g: Graphics2D)

    fun position(newX: Int, newY: Int)

    fun paint(g: Graphics2D)
}