package com.pedropathing.paths

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose

@PathMarker
class KotlinPathBuilder internal constructor(
    private val pathConstraints: PathConstraints,
    private val globalHeadingInterpolator: HeadingInterpolator?
) {
    private val pathChain = ArrayList<Path>()

    fun path(
        pathConstraints: PathConstraints = this.pathConstraints,
        interpolator: HeadingInterpolator = HeadingInterpolator.tangent,
        curveFactory: (List<Pose>, PathConstraints) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) {
        val path = KotlinPath(pathConstraints, curveFactory)
        path.init()

        pathChain += path.build().apply { setHeadingInterpolation(interpolator)  }
    }

    fun pathConstantHeading(
        constantHeading: Double,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>, PathConstraints) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.constant(constantHeading), curveFactory, init)

    fun pathLinearHeading(
        startHeading: Double,
        endHeading: Double,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>, PathConstraints) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.linear(startHeading, endHeading, endTime), curveFactory, init)

    fun pathFacingPoint(
        pose: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>, PathConstraints) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.facingPoint(pose), curveFactory, init)

    fun pathFacingPoint(
        x: Double,
        y: Double,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>, PathConstraints) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.facingPoint(x, y), curveFactory, init)

    internal fun build(): PathChain {
        val pathChain = PathChain(pathChain)
        if (globalHeadingInterpolator != null) {
            pathChain.setHeadingInterpolator(globalHeadingInterpolator)
        }
        return pathChain
    }
}

fun pathChain(
    pathConstraints: PathConstraints = PathConstraints.defaultConstraints,
    globalHeadingInterpolator: HeadingInterpolator? = null,
    init: KotlinPathBuilder.() -> Unit,
): PathChain {
    val builder = KotlinPathBuilder(pathConstraints, globalHeadingInterpolator)
    builder.init()
    return builder.build()
}