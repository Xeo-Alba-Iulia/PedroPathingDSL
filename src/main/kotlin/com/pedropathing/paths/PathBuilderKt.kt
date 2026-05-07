package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.callbacks.PathCallback

@PathMarker
class PathBuilderKt (
    val follower: Follower,
    val decelerationType: PathChain.DecelerationType,
    val pathConstraints: PathConstraints,
    val globalHeadingInterpolator: HeadingInterpolator?
) {
    private val pathChain = ArrayList<Path>()
    private val callbacks = ArrayList<PathCallback>()

    fun addPath(path: Path, block: CallbackBuilderKt.() -> Unit = {}) {
        callbacks += CallbackBuilderKt(follower, pathChain.size, path.curve).apply(block).callbacks
        pathChain += path
    }

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
     */
    fun path(
        vararg points: Pose,
        interpolator: HeadingInterpolator = HeadingInterpolator.tangent,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) {
        val path = Path(createCurve(points.toList()), pathConstraints).apply { setHeadingInterpolation(interpolator) }
        addPath(path, block)
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
        heading: Double,
        vararg points: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = path(
        *points,
        interpolator = HeadingInterpolator.constant(heading),
        pathConstraints = pathConstraints,
        block = block,
    )

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
        vararg points: Pose,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = path(
        *points,
        interpolator = HeadingInterpolator.linear(startHeading, endHeading, endTime),
        pathConstraints = pathConstraints,
        block = block,
    )

    /**
     * Creates a path with a linear heading interpolation between the headings of the first and last control points.
     */
    @PathLinearExperimental
    fun pathLinearHeading(
        vararg points: Pose,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = pathLinearHeading(
        points.first().heading,
        points.last().heading,
        *points,
        endTime = endTime,
        pathConstraints = pathConstraints,
        block = block,
    )

    /**
     * Creates a path from the last control point of the previous path to the specified pose,
     * with a linear heading interpolation between the headings of the start and end poses.
     */
    @PathLinearExperimental
    fun pathToPose(
        pose: Pose,
        endTime: Double = 1.0,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = pathLinearHeading(
        pathChain.lastOrNull()?.lastControlPoint ?: error("No previous path to get the last pose from"),
        pose,
        endTime = endTime,
        pathConstraints = pathConstraints,
        block = block,
    )

    /**
     * Shortcut for
     * ```
     * path(interpolator = HeadingInterpolator.facingPoint(pose))
     * ```
     *
     * @see path
     */
    fun pathFacingPoint(
        targetPose: Pose,
        vararg points: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = path(
        *points,
        interpolator = HeadingInterpolator.facingPoint(targetPose),
        pathConstraints = pathConstraints,
        block = block,
    )

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
        vararg points: Pose,
        pathConstraints: PathConstraints = this.pathConstraints,
        block: CallbackBuilderKt.() -> Unit = {},
    ) = path(
        *points,
        interpolator = HeadingInterpolator.facingPoint(x, y),
        pathConstraints = pathConstraints,
        block = block,
    )

    internal fun build() = PathChain(pathChain).apply {
        globalHeadingInterpolator?.let { setHeadingInterpolator(it) }
        decelerationType = this@PathBuilderKt.decelerationType
        callbacks = this@PathBuilderKt.callbacks
    }
}

