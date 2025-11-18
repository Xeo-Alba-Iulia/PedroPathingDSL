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
    private val decelerationType: PathChain.DecelerationType,
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
        callbacks += pathCallbackFactories.map { it(pathChain.size, follower, builtPath.curve) }
        pathChain += builtPath.apply { setHeadingInterpolation(interpolator); setConstraints(pathConstraints) }
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

    @PathLinearExperimental
    fun pathLinearHeading(
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) {
        val path = KotlinPath(curveFactory)
        path.init()
        val (builtPath, pathCallbackFactories) = path.build()

        val startHeading = builtPath.firstControlPoint.heading
        val endHeading = builtPath.lastControlPoint.heading
        val interpolator = HeadingInterpolator.linear(startHeading, endHeading, endTime)

        callbacks += pathCallbackFactories.map { it(pathChain.size, follower, builtPath.curve) }
        pathChain += builtPath.apply { setHeadingInterpolation(interpolator); setConstraints(pathConstraints) }
    }

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

    internal fun build() = PathChain(pathChain).apply {
        globalHeadingInterpolator?.let { setHeadingInterpolator(it) }
        decelerationType = this@KotlinPathBuilder.decelerationType
        callbacks = this@KotlinPathBuilder.callbacks
    }
}

fun pathChain(
    follower: Follower?,
    decelerationType: PathChain.DecelerationType = PathChain.DecelerationType.LAST_PATH,
    pathConstraints: PathConstraints = PathConstraints.defaultConstraints,
    globalHeadingInterpolator: HeadingInterpolator? = null,
    init: KotlinPathBuilder.() -> Unit,
): PathChain {
    val builder = KotlinPathBuilder(follower, decelerationType, pathConstraints, globalHeadingInterpolator)
    builder.init()
    return builder.build()
}