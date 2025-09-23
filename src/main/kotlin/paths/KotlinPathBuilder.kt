package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.callbacks.PathCallback

internal typealias CallbackFactory = (Int, Follower?, Curve) -> PathCallback

@PathMarker
class KotlinPathBuilder internal constructor(
    private val follower: Follower?,
    private val pathConstraints: PathConstraints,
    private val globalHeadingInterpolator: HeadingInterpolator?
) {
    private val pathChain = ArrayList<Path>()
    private val callbacks = ArrayList<PathCallback>()

    fun path(
        pathConstraints: PathConstraints = this.pathConstraints,
        interpolator: HeadingInterpolator = HeadingInterpolator.tangent,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) {
        val path = KotlinPath(curveFactory)
        path.init()
        val (builtPath, pathCallbackFactories) = path.build()
        callbacks += pathCallbackFactories.map {
            it(pathChain.size, follower, builtPath.curve)
        }
        pathChain += builtPath.apply {
            setHeadingInterpolation(interpolator)
            setConstraints(pathConstraints)
        }
    }

    fun pathConstantHeading(
        constantHeading: Double,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.constant(constantHeading), curveFactory, init)

    fun pathLinearHeading(
        startHeading: Double,
        endHeading: Double,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.linear(startHeading, endHeading, endTime), curveFactory, init)

    fun pathFacingPoint(
        pose: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.facingPoint(pose), curveFactory, init)

    fun pathFacingPoint(
        x: Double,
        y: Double,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.facingPoint(x, y), curveFactory, init)

    internal fun build(): PathChain {
        val pathChain = PathChain(pathChain)
        globalHeadingInterpolator?.let { pathChain.setHeadingInterpolator(it) }
        return pathChain
    }
}

fun pathChain(
    follower: Follower?,
    pathConstraints: PathConstraints = PathConstraints.defaultConstraints,
    globalHeadingInterpolator: HeadingInterpolator? = null,
    init: KotlinPathBuilder.() -> Unit,
): PathChain {
    val builder = KotlinPathBuilder(follower, pathConstraints, globalHeadingInterpolator)
    builder.init()
    return builder.build()
}