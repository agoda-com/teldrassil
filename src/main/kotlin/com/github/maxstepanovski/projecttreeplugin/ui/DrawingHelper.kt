package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.ui.model.Attributes
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.geom.Line2D

fun drawConnection(from: GraphNodeView, to: GraphNodeView, g: Graphics2D) {
    g.drawLine(
            from.outEdgesX,
            from.outEdgesY,
            to.inEdgesX,
            to.inEdgesY
    )
    drawArrow(
            g,
            Attributes(20.0, 25.0, Color.BLACK),
            Point(from.outEdgesX, from.outEdgesY),
            Point(to.inEdgesX, to.inEdgesY)
    )
}

fun drawCenteredConnection(from: GraphNodeView, to: GraphNodeView, g: Graphics2D) {
    // two points make a line connecting centers of two Nodes
    val center1 = Point(from.inEdgesX, from.inEdgesY + from.height / 2)
    val center2 = Point(to.inEdgesX, to.inEdgesY + to.height / 2)

    // sides of first rectangle node
    val vertices1 = listOf(
            Point(from.x, from.y),
            Point(from.x + from.width, from.y),
            Point(from.x + from.width, from.y + from.height),
            Point(from.x, from.y + from.height)
    )

    // sides of second rectangle node
    val vertices2 = listOf(
            Point(to.x, to.y),
            Point(to.x + to.width, to.y),
            Point(to.x + to.width, to.y + to.height),
            Point(to.x, to.y + to.height)
    )

    val first = lineRectangleIntersection(center1, center2, vertices1)
    val second = lineRectangleIntersection(center1, center2, vertices2)

    if (first != null && second != null) {
        g.drawLine(first.x, first.y, second.x, second.y)
        drawArrow(
                g,
                Attributes(20.0, 25.0, Color.BLACK),
                first,
                second
        )
    }
}

fun twoLinesIntersection(startA: Point, endA: Point, startB: Point, endB: Point): Point {
    // Line AB represented as a1x + b1y = c1
    val a1 = (endA.y - startA.y).toDouble()
    val b1 = (startA.x - endA.x).toDouble()
    val c1 = a1 * startA.x + b1 * startA.y

    // Line CD represented as a2x + b2y = c2
    val a2 = (endB.y - startB.y).toDouble()
    val b2 = (startB.x - endB.x).toDouble()
    val c2 = a2 * startB.x + b2 * startB.y
    val determinant = a1 * b2 - a2 * b1
    return if (determinant == 0.0) {
        // The lines are parallel. This is simplified
        // by returning a pair of FLT_MAX
        Point(Double.MAX_VALUE.toInt(), Double.MAX_VALUE.toInt())
    } else {
        val x = (b2 * c1 - b1 * c2) / determinant
        val y = (a1 * c2 - a2 * c1) / determinant
        Point(x.toInt(), y.toInt())
    }
}

fun lineRectangleIntersection(lineStart: Point, lineEnd: Point, rectVertices: List<Point>): Point? {
    for (i in rectVertices.indices) {
        val sideStart = rectVertices[i]
        val sideEnd = rectVertices[(i + 1) % rectVertices.size]
        val doesSideIntersectWithLine = Line2D.linesIntersect(
                lineStart.x.toDouble(),
                lineStart.y.toDouble(),
                lineEnd.x.toDouble(),
                lineEnd.y.toDouble(),
                sideStart.x.toDouble(),
                sideStart.y.toDouble(),
                sideEnd.x.toDouble(),
                sideEnd.y.toDouble()
        )
        if (doesSideIntersectWithLine) {
            return twoLinesIntersection(
                    lineStart,
                    lineEnd,
                    sideStart,
                    sideEnd
            )
        }
    }
    return null
}