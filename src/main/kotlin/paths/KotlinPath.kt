package com.pedropathing.paths

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose

@PathMarker
class KotlinPath internal constructor(
    private val pathConstraints: PathConstraints,
    private val curveFactory: (List<Pose>, PathConstraints) -> Curve,
) {
    private val pathPoints = mutableListOf<Pose>()

    operator fun Pose.unaryPlus() {
        pathPoints += this
    }

    internal fun build() = Path(
        when (pathPoints.size) {
            0 -> throw IllegalStateException("A path must have at least one point")
            1 -> BezierPoint(pathPoints.first())
            2 -> BezierLine(pathPoints.first(), pathPoints.last())
            else -> curveFactory(pathPoints, pathConstraints)
        }
    )
}