package com.github.maxstepanovski.projecttreeplugin.ui

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import kotlin.math.roundToInt

data class TextLabel(
        val text: String
) : Paintable {
    var xPadding: Int = 5
    var yPadding: Int = 5
    var strokeWidth: Float = 1F
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
        val oldStroke = g.stroke
        var oldPaint = g.paint
        g.paint = Color.WHITE
        g.fill(
                Rectangle2D.Float(
                        x.toFloat(),
                        y.toFloat(),
                        width.toFloat(),
                        height.toFloat()
                )
        )
        g.paint = oldPaint

        oldPaint = g.paint
        g.paint = color
        g.stroke = BasicStroke(strokeWidth)
        g.drawString(text, x + xPadding, y + textHeight + yPadding)

        g.paint = oldPaint
        g.stroke = oldStroke
    }
}