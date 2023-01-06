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

    private var fontScale: Float = 2.0F
    private var x: Int = 0
    private var y: Int = 0
    private var textHeight: Int = 0

    override fun size(g: Graphics2D) {
        val oldFont = g.font
        g.font = oldFont.deriveFont(oldFont.size * fontScale)
        val rect = g.fontMetrics.getStringBounds(text, g)
        textHeight = rect.height.roundToInt()
        width = rect.width.roundToInt() + (2 * xPadding)
        height = rect.height.roundToInt() + (2 * yPadding)
        g.font = oldFont
    }

    override fun position(newX: Int, newY: Int) {
        x = newX
        y = newY
    }

    override fun paint(g: Graphics2D) {
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
        val oldStroke = g.stroke
        g.stroke = BasicStroke(strokeWidth)
        val oldFont = g.font
        g.font = oldFont.deriveFont(oldFont.size * fontScale)

        g.drawString(text, x + xPadding, y + textHeight + yPadding)

        g.font = oldFont
        g.paint = oldPaint
        g.stroke = oldStroke
    }

    fun increaseFontSize() {
        fontScale *= 1.2F
    }

    fun decreaseFontSize() {
        fontScale *= 0.8F
    }
}