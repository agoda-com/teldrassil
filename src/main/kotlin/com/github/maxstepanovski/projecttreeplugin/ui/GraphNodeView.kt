package com.github.maxstepanovski.projecttreeplugin.ui

import com.intellij.openapi.rd.draw2DRect
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle
import kotlin.math.max

data class GraphNodeView(
        val id: String,
        val name: String,
        val fields: List<String>,
        val methods: List<String>
) : Paintable {
    val childNodes = mutableListOf<GraphNodeView>()
    var sectionGap: Int = 10
    var lineGap: Int = 5
    var xPadding: Int = 5
    var yPadding: Int = 5
    var strokeWidth: Double = 1.0
    var color: Color = Color.BLUE

    var x: Int = 0
        private set
    var y: Int = 0
        private set

    var inEdgesX: Int = 0
        get() = x + width / 2
        private set
    var inEdgesY: Int = 0
        get() = y
        private set
    var outEdgesX: Int = 0
        get() = x + width / 2
        private set
    var outEdgesY: Int = 0
        get() = y + height
        private set
    var width: Int = 0
        get() = field
        private set
    var height: Int = 0
        get() = field
        private set

    private val nameTextLabel: TextLabel = TextLabel(name)
    private val fieldTextLabels: MutableList<TextLabel> = fields.fold(mutableListOf()) { acc, field ->
        acc.add(TextLabel(field))
        acc
    }
    private val methodTextLabels: MutableList<TextLabel> = methods.fold(mutableListOf()) { acc, method ->
        acc.add(TextLabel(method))
        acc
    }

    override fun size(g: Graphics2D) {
        var maxWidth = 0
        var currentY = 0

        nameTextLabel.let {
            it.size(g)
            maxWidth = max(it.width, maxWidth)
            currentY += it.height
        }

        currentY += sectionGap

        fieldTextLabels.forEach {
            it.size(g)
            maxWidth = max(it.width, maxWidth)
            currentY += it.height
            currentY += lineGap
        }

        currentY += sectionGap

        methodTextLabels.forEach {
            it.size(g)
            maxWidth = max(it.width, maxWidth)
            currentY += it.height
            currentY += lineGap
        }

        currentY += sectionGap

        width = maxWidth + 2 * xPadding
        height = currentY + yPadding
    }

    override fun position(newX: Int, newY: Int) {
        x = newX
        y = newY

        val currentX = x + xPadding
        var currentY = y + yPadding

        nameTextLabel.let {
            it.position(currentX, currentY)
            currentY += it.height + lineGap
        }

        currentY += sectionGap

        fieldTextLabels.forEach {
            it.position(currentX, currentY)
            currentY += it.height + lineGap
        }

        currentY += sectionGap

        methodTextLabels.forEach {
            it.position(currentX, currentY)
            currentY += it.height + lineGap
        }

        currentY += sectionGap
    }

    override fun paint(g: Graphics2D) {
        g.draw2DRect(
                Rectangle(x, y, width, height),
                strokeWidth,
                color
        )
        nameTextLabel.paint(g)
        fieldTextLabels.forEach {
            it.paint(g)
        }
        methodTextLabels.forEach {
            it.paint(g)
        }
    }
}
