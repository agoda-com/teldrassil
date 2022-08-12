package com.github.maxstepanovski.projecttreeplugin.ui

import com.intellij.openapi.rd.draw2DRect
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import kotlin.math.roundToInt

data class TextLabel(
        val text: String
) : Paintable {
    var xPadding: Int = 5
    var yPadding: Int = 5
    var strokeWidth: Double = 1.0
    var color = Color.BLACK
    var width: Int = 0
        get() = field
        private set
    var height: Int = 0
        get() = field
        private set

    private var x: Int = 0
    private var y: Int = 0
    private var textHeight: Int = 0

    override fun size(g: Graphics2D) {
        val rect = g.fontMetrics.getStringBounds(text, g)
        textHeight = rect.height.roundToInt()
        width = rect.width.roundToInt() + (2 * xPadding)
        height = rect.height.roundToInt() + (2 * yPadding)
    }

    override fun position(newX: Int, newY: Int) {
        x = newX
        y = newY
    }

    override fun paint(g: Graphics2D) {
        g.draw2DRect(
                Rectangle(x, y, width, height),
                strokeWidth,
                color
        )
        g.drawString(text, x + xPadding, y + textHeight + yPadding)
    }
}