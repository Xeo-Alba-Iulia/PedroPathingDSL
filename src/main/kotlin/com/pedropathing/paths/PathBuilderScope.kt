package com.pedropathing.paths

import com.pedropathing.api.Paths
import com.pedropathing.config.Modifier
import com.pedropathing.math.Pose
import com.pedropathing.paths.callbacks.Callback
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.interpolator.Interpolator

@PathMarker
class PathBuilderScope @PublishedApi internal constructor() {
    inline fun path(
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: PathBuilderScope.() -> Unit
    ) {
        val (path, callbacks) = PathBuilderScope().apply(block).build()
        paths += applyHeadingAndModifiers(path, interpolator, modifiers)
        this.callbacks += callbacks
    }

    inline fun path(
        curve: Curve,
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        val path = Paths.path(curve).heading(interpolator).let { path ->
            if (modifiers.isNotEmpty()) path.with(modifiers) else path
        }
        paths += path
        addCallbacks(path.curve, block)
    }

    inline fun path(
        vararg points: Pose,
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(createCurve(points), interpolator, modifiers, block)

    inline fun tangent(
        vararg points: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.tangent, block = block)

    inline fun linear(
        vararg points: Pose,
        startHeading: Double,
        endHeading: Double,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = getHeadingInterpolator(startHeading, endHeading), block = block)

    inline fun linear(
        vararg points: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = linear(
        *points,
        startHeading = points.first().heading(),
        endHeading = points.last().heading(),
        block = block
    )

    inline fun constant(
        vararg points: Pose,
        heading: Double,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.constant(heading), block = block)

    /**
     * Adds a path with constant heading interpolation.
     *
     * The heading of the first and last points must match exactly.
     *
     * @param points The points defining the curve.
     * @throws IllegalStateException If the start and end point headings do not match.
     */
    inline fun constant(
        vararg points: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        require(points.first().heading() == points.last().heading()) { "End points must have the same heading" }
        return constant(*points, heading = points.first().heading(), block = block)
    }

    inline fun facingPoint(
        vararg points: Pose,
        target: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.facingPoint(target), block = block)

    /**
     * A convenience helper for generating a line path from the last endpoint to the given target.
     *
     * @param endPoint The target pose to which a path will be generated.
     * @param interpolator Path heading interpolator.
     *                     By default, it will be set to a linear interpolator between
     *                     the last heading and the target heading.
     * @param block A lambda to define path callbacks within the generated path context.
     * @throws IllegalStateException If no paths exist before calling this function.
     */
    inline fun pathToPoint(
        endPoint: Pose,
        interpolator: Interpolator? = getHeadingInterpolator(lastHeading, endPoint.heading()),
        block: CallbackBuilderScope.() -> Unit = {}
    ) =
        paths
            .lastOrNull()
            ?.let { lastPath ->
                path(
                    lastPath.curve.endPoint().toPose(), endPoint,
                    interpolator = interpolator,
                    block = block
                )
            } ?: throw IllegalStateException(EMPTY_PATHS_ERR_MSG)

    val lastHeading get() =
        paths.lastOrNull()?.heading(1.0) ?: throw IllegalStateException(EMPTY_PATHS_ERR_MSG)

    @PublishedApi internal val paths = mutableListOf<Path>()
    @PublishedApi internal val callbacks = mutableMapOf<Curve, MutableList<Callback>>()

    @PublishedApi internal inline fun addCallbacks(curve: Curve, block: CallbackBuilderScope.() -> Unit) {
        CallbackBuilderScope(curve)
            .apply(block)
            .build()
            .takeIf { it.isNotEmpty() }
            ?.let { callbacks += curve to it }
    }

    @PublishedApi internal fun build() = Pair(Paths.path(*paths.toTypedArray()), callbacks)

    companion object {
        @PublishedApi internal const val EMPTY_PATHS_ERR_MSG = "No paths have been created yet"
    }
}
