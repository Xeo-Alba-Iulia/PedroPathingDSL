package com.pedropathing.api

import com.pedropathing.config.Modifier
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import com.pedropathing.callbacks.Callback
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.curves.bezier.BezierCurve
import com.pedropathing.paths.interpolator.Interpolator

@PathMarker
class PathBuilderScope @PublishedApi internal constructor() {
    /**
     * Starts a new scope for defining a path.
     *
     * This function should only be used to change the [modifiers] or [interpolator] of a subset of paths.
     *
     * @param interpolator Interpolator to be applied globally to all the paths created within this scope.
     * @param modifiers List of modifiers to be applied globally to all the paths created within this scope.
     *
     * @throws AssertionError If neither [interpolator] nor [modifiers] are provided.
     */
    inline fun path(
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: PathBuilderScope.() -> Unit
    ) {
        assert(interpolator != null || modifiers.isNotEmpty()) { "Either interpolator or modifiers must be provided" }
        val (path, callbacks) = PathBuilderScope().apply(block).build()
        paths += applyHeadingAndModifiers(path, interpolator, modifiers)
        this.callbacks += callbacks
    }

    /**
     * Creates a path from a given [curve].
     *
     * Only use this function if you have a custom curve implementation that you want to use.
     *
     * @param curve The curve to be used for the path.
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun path(
        curve: Curve,
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        val path = applyHeadingAndModifiers(Paths.path(curve), interpolator, modifiers)
        paths += path
        addCallbacks(path.curve, block)
    }

    /**
     * Creates a path from a list of [points].
     * This function is a shorthand for creating
     * either a [com.pedropathing.paths.curves.Line] o a [BezierCurve]
     * from the given points.
     *
     * @param points The points defining the curve.
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun path(
        vararg points: Pose,
        interpolator: Interpolator? = null,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(createCurve(points), interpolator, modifiers, block)

    /**
     * Creates a path with a tangent heading interpolation.
     *
     *
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun tangent(
        vararg points: Pose,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.tangent, modifiers = modifiers, block = block)

    /**
     * Creates a path with linear heading interpolation.
     *
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun linear(
        vararg points: Pose,
        startHeading: Double,
        endHeading: Double,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = getHeadingInterpolator(startHeading, endHeading), modifiers = modifiers, block = block)

    /**
     * Creates a path with linear heading interpolation.
     *
     * The heading of the first and last point will be used as the start and end headings.
     *
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun linear(
        vararg points: Pose,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = getHeadingInterpolator(*points), modifiers = modifiers, block = block)

    /**
     * Creates a path with constant heading interpolation.
     *
     * @param points The points defining the curve.
     * @param heading The constant heading to be maintained along the path.
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun constant(
        vararg points: Pose,
        heading: Double,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.constant(heading), modifiers = modifiers, block = block)

    /**
     * Adds a path with constant heading interpolation.
     *
     * The heading of the first and last points must match exactly.
     *
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     * @throws IllegalStateException If the start and end point headings do not match.
     */
    inline fun constant(
        vararg points: Pose,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        require(points.size > 1 && points.first().heading() == points.last().heading()) {
            if (points.size <= 1) NOT_ENOUGH_POINTS_ERR_MSG
            else "The start and end point headings must match exactly"
        }
        return constant(*points, heading = points.first().heading(), modifiers = modifiers, block = block)
    }

    /**
     * Creates a path with a heading interpolation that faces the given [target].
     *
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun facingPoint(
        vararg points: Pose,
        target: Pose,
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.facingPoint(target), modifiers = modifiers, block = block)

    /**
     * A convenience helper for generating a line path from the last endpoint to the given target.
     *
     * @param endPoint The target pose to which a path will be generated.
     * @param interpolator Path heading interpolator.
     *                     By default, it will be set to a linear interpolator between
     *                     the last heading and the target heading.
     * @param block An optional context block that can be used to define callbacks for the path.
     * @throws IllegalStateException If no paths exist before calling this function.
     * @see CallbackBuilderScope
     */
    inline fun pathToPoint(
        endPoint: Pose,
        interpolator: Interpolator? = getHeadingInterpolator(lastHeading, endPoint.heading()),
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = paths
            .lastOrNull()
            ?.let { lastPath ->
                path(
                    lastPath.curve.endPoint().toPose(), endPoint,
                    interpolator = interpolator,
                    modifiers = modifiers,
                    block = block
                )
            } ?: throw NoSuchElementException(EMPTY_PATHS_ERR_MSG)

    /**
     * Creates a path that passes through the given [points].
     *
     * If no [interpolator] is provided, it will be set to a linear interpolator between the first and last point headings.
     * @param interpolator Interpolator to be used for the path.
     *                     Defaults to a linear interpolator between the first and last points headings.
     * @param block An optional context block that can be used to define callbacks for the path.
     * @see CallbackBuilderScope
     */
    inline fun through(
        vararg points: Pose,
        interpolator: Interpolator? = getHeadingInterpolator(*points),
        modifiers: List<Modifier> = emptyList(),
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(BezierCurve.through(*points), interpolator, modifiers, block)

    /**
     * Returns the heading at the end of the last path.
     *
     * @throws NoSuchElementException If no paths have been created yet.
     * @throws IllegalStateException If the last path has no heading.
     */
    val lastHeading get() =
        paths.lastOrNull()?.heading(1.0) ?: throw NoSuchElementException(EMPTY_PATHS_ERR_MSG)

    @PublishedApi internal val paths = mutableListOf<Path>()
    @PublishedApi internal val callbacks = mutableMapOf<Curve, MutableList<Callback>>()

    /**
     * Adds callbacks associated with the specified curve to the internal collection of callbacks.
     *
     * This function builds the callbacks defined within the provided block and appends them
     * to the internal collection if any are created.
     *
     * @param curve The curve for which the callbacks are being defined.
     * @param block A lambda within the [CallbackBuilderScope] to define callbacks associated with the curve.
     */
    @PublishedApi internal inline fun addCallbacks(curve: Curve, block: CallbackBuilderScope.() -> Unit) {
        CallbackBuilderScope(curve)
            .apply(block)
            .build()
            .takeIf { it.isNotEmpty() }
            ?.let { callbacks += curve to it }
    }

    /**
     * Builds and returns a pair containing the constructed path and the associated callbacks.
     *
     * This method ensures that at least one path exists before being invoked. If only one path
     * is available, it directly returns that path along with its callbacks. If multiple paths
     * exist, they are combined using `Paths.path()` and the resulting combined path is returned
     * with its callbacks.
     *
     * @return A pair where the first element is the `Path` representing the constructed path(s)
     * and the second element is a mutable map of `Curve` to their respective callbacks.
     * @throws IllegalStateException If no paths have been created prior to invocation.
     */
    @PublishedApi internal fun build(): Pair<Path, MutableMap<Curve, MutableList<Callback>>> {
        check(paths.isNotEmpty()) { "No paths have been created yet" }
        if (paths.size == 1) return paths[0] to callbacks
        return Paths.path(*paths.toTypedArray()) to callbacks
    }

    companion object {
        @PublishedApi internal const val EMPTY_PATHS_ERR_MSG = "No paths have been created yet"
        @PublishedApi internal val NOT_ENOUGH_POINTS_ERR_MSG = "At least two points are required for a path"
    }
}
