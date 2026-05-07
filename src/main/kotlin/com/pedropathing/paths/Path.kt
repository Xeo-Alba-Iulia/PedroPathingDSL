package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose

/**
 * Builds a PathChain using a Kotlin DSL.
 *
 * Example usage:
 *
 * ```
 * val firstPath = follower.pathChain {
 *    path(Pose(0.0, 0.0), Pose(1.0, 1.0)) {
 *        temporalCallback(500.milliseconds) {
 *            println("Reached half a second on this path!")
 *        }
 *    }
 * }
 * ```
 *
 * @return the built PathChain
 */
fun Follower.pathChain(
    decelerationType: PathChain.DecelerationType = PathChain.DecelerationType.LAST_PATH,
    pathConstraints: PathConstraints = constraints,
    globalHeadingInterpolator: HeadingInterpolator? = null,
    block: PathBuilderKt.() -> Unit,
) = PathBuilderKt(this, decelerationType, pathConstraints, globalHeadingInterpolator).apply(block).build()


@JvmOverloads
inline fun createCurve(points: List<Pose>, curveFactory: (List<Pose>) -> Curve = ::BezierCurve) =
    when (points.size) {
        0 -> throw IllegalArgumentException("points cannot be empty")
        1 -> BezierPoint(points.first())
        2 -> BezierLine(points[0], points[1])
        else -> curveFactory(points)
    }
