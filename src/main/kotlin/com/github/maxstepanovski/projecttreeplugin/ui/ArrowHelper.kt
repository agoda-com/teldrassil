package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.ui.model.Arrow
import com.github.maxstepanovski.projecttreeplugin.ui.model.Attributes
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.GeneralPath
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

fun drawLineArrowDirection1(g2d: Graphics2D, arrowAttributes: Attributes, line2D: Line2D.Double) {
    drawLine(g2d, line2D)
    drawArrow(g2d, arrowAttributes, line2D.p1, line2D.p2)
}

fun drawLineArrowDirection2(g2d: Graphics2D, arrowAttributes: Attributes, line2D: Line2D.Double) {
    drawLine(g2d, line2D)
    drawArrow(g2d, arrowAttributes, line2D.p2, line2D.p1)
}

fun drawLineArrowDirectionAll(g2d: Graphics2D, arrowAttributes: Attributes, line2D: Line2D.Double) {
    drawLine(g2d, line2D)
    drawArrow(g2d, arrowAttributes, line2D.p1, line2D.p2)
    drawArrow(g2d, arrowAttributes, line2D.p2, line2D.p1)
}

fun drawLine(g2d: Graphics2D, line2D: Line2D.Double?) {
    g2d.color = Color.BLACK
    g2d.draw(line2D)
}

fun drawArrow(g2d: Graphics2D, arrowAttributes: Attributes, point1: Point2D, point2: Point2D) {
    //Get Arrow instance
    val arrow = getArrow(arrowAttributes, point1, point2)

    //Build GeneralPath
    val arrow2D = GeneralPath()
    arrow2D.moveTo(arrow.point1.x, arrow.point1.y)
    arrow2D.lineTo(arrow.point2.x, arrow.point2.y)
    arrow2D.lineTo(arrow.point3.x, arrow.point3.y)
    arrow2D.closePath()

    //draw
    g2d.color = arrow.attributes.color
    g2d.fill(arrow2D)
}

fun getArrow(arrowAttributes: Attributes, point1: Point2D, point2: Point2D): Arrow {
    val arrow = Arrow(arrowAttributes)

    //Calculate the hypotenuse
    val hypotenuse = arrow.attributes.height / Math.cos(Math.toRadians(arrow.attributes.angle / 2))

    //Calculate the quadrant of the current line
    var quadrant = -1
    if (point1.x > point2.x && point1.y < point2.y) {
        quadrant = 1
    } else if (point1.x < point2.x && point1.y < point2.y) {
        quadrant = 2
    } else if (point1.x < point2.x && point1.y > point2.y) {
        quadrant = 3
    } else if (point1.x > point2.x && point1.y > point2.y) {
        quadrant = 4
    }

    //Calculate the angle of the line
    var linAngle = getLineAngle(point1.x, point1.y, point2.x, point2.y)
    if (java.lang.Double.isNaN(linAngle)) {
        //The line is perpendicular to the x axis
        if (point1.x == point2.x) {
            linAngle = if (point1.y < point2.y) {
                90.0
            } else {
                270.0
            }
            quadrant = 2
        }
    } else if (linAngle == 0.0) {
        if (point1.y == point2.y) {
            linAngle = if (point1.x < point2.x) {
                0.0
            } else {
                180.0
            }
            quadrant = 2
        }
    }

    //Upper half arrow
    val xAngle = linAngle - arrow.attributes.angle / 2 //angle with x axis
    var py0 = hypotenuse * sin(Math.toRadians(xAngle)) //Calculate the increment in the y direction
    var px0 = hypotenuse * cos(Math.toRadians(xAngle)) //Calculate the increment in the x direction

    //lower half arrow
    val yAngle = 90 - linAngle - arrow.attributes.angle / 2 //Angle with y axis
    var px1 = hypotenuse * sin(Math.toRadians(yAngle))
    var py1 = hypotenuse * cos(Math.toRadians(yAngle))

    //first quadrant
    if (quadrant == 1) {
        px0 = -px0
        px1 = -px1
    } else if (quadrant == 2) {
        //do nothing
    } else if (quadrant == 3) {
        py0 = -py0
        py1 = -py1
    } else if (quadrant == 4) {
        py0 = -py0
        px0 = -px0
        px1 = -px1
        py1 = -py1
    }

    //build
    arrow.point1 = Point2D.Double()
    arrow.point1.x = point1.x
    arrow.point1.y = point1.y
    arrow.point2 = Point2D.Double()
    arrow.point2.x = point1.x + px0
    arrow.point2.y = point1.y + py0
    arrow.point3 = Point2D.Double()
    arrow.point3.x = point1.x + px1
    arrow.point3.y = point1.y + py1
    return arrow
}

private fun getLineAngle(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    val k1 = (y2 - y1) / (x2 - x1)
    val k2 = 0.0
    return abs(Math.toDegrees(atan((k2 - k1) / (1 + k1 * k2))))
}
