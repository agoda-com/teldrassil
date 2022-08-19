package com.github.maxstepanovski.projecttreeplugin.ui.model

import java.awt.Color
import java.awt.geom.Point2D

data class Arrow(
        var attributes: Attributes,
        var point1: Point2D.Double = Point2D.Double(),
        var point2: Point2D.Double = Point2D.Double(),
        var point3: Point2D.Double = Point2D.Double()
)

data class Attributes(
        var height: Double,
        var angle: Double,
        var color: Color
)