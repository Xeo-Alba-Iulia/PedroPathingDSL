package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
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

    /**
     * Creates a path and adds it to the path chain.
     *
     * Example usage:
     * ```
     * path {
     *    +Pose(0.0, 0.0) // Add control points using unaryPlus.
     *    +Pose(1.0, 1.0) // You can use the return value checker from kotlin 2.3.0
     *    callbacks {...} // to ensure that the plus sign is not forgotten.
     * }
     * ```
     * @param pathConstraints the constraints to apply to this path
     * @param interpolator the heading interpolator to use for this path
     * @param curveFactory This factory is used to allow custom Curves to be created. Only takes effect if the path has
     * more than 2 control points.
     *
     * @param init the DSL block to build the path. Poses can be added using [KotlinPath.unaryPlus]
     */
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

    /**
     * Shortcut for
     * ```
     * path(interpolator = HeadingInterpolator.constant(constantHeading))
     * ```
     *
     * @see path
     */
    fun pathConstantHeading(
        constantHeading: Double,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.constant(constantHeading), curveFactory, init)

    /**
     * Shortcut for
     * ```
     * path(interpolator = HeadingInterpolator.linear(startHeading, endHeading, endTime))
     * ```
     *
     * @see path
     */
    fun pathLinearHeading(
        startHeading: Double,
        endHeading: Double,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.linear(startHeading, endHeading, endTime), curveFactory, init)

    /**
     * Creates a path with a linear heading interpolation between the headings of the first and last control points.
     */
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

    /**
     * Creates a path from the last control point of the previous path to the specified pose,
     * with a linear heading interpolation between the headings of the start and end poses.
     */
    @PathLinearExperimental
    fun pathToPose(
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        pose: Pose,
    ) {
        val lastPose = pathChain.lastOrNull()?.lastControlPoint ?: error("No previous path to get starting pose from")
        val path = Path(BezierLine(lastPose, pose), pathConstraints).apply {
            setHeadingInterpolation(HeadingInterpolator.linear(lastPose.heading, pose.heading))
        }
        pathChain += path
    }

    /**
     * Shortcut for
     * ```
     * path(interpolator = HeadingInterpolator.facingPoint(pose))
     * ```
     *
     * @see path
     */
    fun pathFacingPoint(
        pose: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        curveFactory: (List<Pose>) -> Curve = ::BezierCurve,
        init: KotlinPath.() -> Unit,
    ) = path(pathConstraints, HeadingInterpolator.facingPoint(pose), curveFactory, init)

    /**
     * Shortcut for
     * ```
     * path(interpolator = HeadingInterpolator.facingPoint(x, y))
     * ```
     *
     * @see path
     */
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

/**
 * Builds a PathChain using a Kotlin DSL.
 *
 * Example usage:
 *
 * ```
 * val firstPath = pathChain(null) {
 *    path {
 *        +Pose(0.0, 0.0)
 *        +Pose(1.0, 1.0)
 *        +Pose(2.0, 0.0)
 *        callbacks {
 *            temporalCallback(500.milliseconds) {
 *                println("Reached half a second on this path!")
 *            }
 *        }
 *    }
 * }
 * ```
 *
 * @param follower The follower is used for [ParametricCallback][com.pedropathing.paths.callbacks.ParametricCallback]
 * and [PoseCallback][com.pedropathing.paths.callbacks.PoseCallback]. It can be null if these callbacks are not used.
 *
 * @param init the DSL block to build the PathChain
 * @return the built PathChain
 */
fun pathChain(
    follower: Follower?,
    decelerationType: PathChain.DecelerationType = PathChain.DecelerationType.LAST_PATH,
    pathConstraints: PathConstraints = PathConstraints.defaultConstraints,
    globalHeadingInterpolator: HeadingInterpolator? = null,
    init: KotlinPathBuilder.() -> Unit,
) = KotlinPathBuilder(follower, decelerationType, pathConstraints, globalHeadingInterpolator).apply { init() }.build()