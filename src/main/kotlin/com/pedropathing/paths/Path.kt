package com.pedropathing.paths

import com.pedropathing.math.Pose
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.curves.Line
import com.pedropathing.paths.curves.bezier.BezierCurve

fun createCurve(points: Array<out Pose>): Curve {
    require(points.size > 1) { "Must have at least 2 points to create a curve" }
    if (points.size == 2) return Line(points[0], points[1])
    return BezierCurve(*points)
}
