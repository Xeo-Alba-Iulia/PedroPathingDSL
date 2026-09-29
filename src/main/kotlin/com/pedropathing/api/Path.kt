package com.pedropathing.api

import com.pedropathing.config.Modifier
import com.pedropathing.math.Pose
import com.pedropathing.paths.Path
import com.pedropathing.callbacks.callbacksMap
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.curves.Line
import com.pedropathing.paths.curves.bezier.BezierCurve
import com.pedropathing.paths.interpolator.Interpolator
import org.jetbrains.annotations.Contract

/**
 * Creates a new [com.pedropathing.paths.Path] from the given [block].
 *
 * @param interpolator Global interpolator to be applied to the path.
 *                     All the functions defined in [PathBuilderScope] also take an optional [interpolator] parameter.
 *                     Use that to modify that specific path's heading interpolation.
 * @param modifiers Global modifiers to be applied to the path.
 *                  All the functions defined in [PathBuilderScope] also take an optional [modifiers] parameter.
 *                  Use that to modify that specific path's constants.
 * @param block A context block that defines the path.
 * @see PathBuilderScope
 */
@Contract("_, _, _ -> new", pure = false)
inline fun path(
    interpolator: Interpolator? = null,
    modifiers: List<Modifier> = emptyList(),
    block: PathBuilderScope.() -> Unit
): Path {
    val (path, currentCallbacks) = PathBuilderScope().apply(block).build()
    val pathWithModifiers = applyHeadingAndModifiers(path, interpolator, modifiers)
    callbacksMap += currentCallbacks
    return pathWithModifiers
}

/**
 * Applies the given [interpolator] and [modifiers] to the given [path].
 */
fun applyHeadingAndModifiers(path: Path, interpolator: Interpolator?, modifiers: List<Modifier>): Path {
    val path = if (interpolator != null) path.heading(interpolator) else path
    return if (modifiers.isNotEmpty()) path.with(modifiers) else path
}

/**
 * Creates a curve from the given [points].
 *
 * If there are only 2 points, it will return a [Line] curve.
 * Otherwise, it will return a [BezierCurve].
 *
 * @throws IllegalArgumentException If there are less than 2 points.
 */
fun createCurve(points: Array<out Pose>): Curve {
    require(points.size > 1) { "Must have at least 2 points to create a curve" }
    if (points.size == 2) return Line(points[0], points[1])
    return BezierCurve(*points)
}

/**
 * Optimization function that returns a constant interpolator if the start and end headings are the same.
 */
fun getHeadingInterpolator(startHeading: Double, endHeading: Double): Interpolator =
    if (startHeading == endHeading) Interpolator.constant(startHeading)
    else Interpolator.linear(startHeading, endHeading)

/**
 * Returns a linear interpolator based on the headings of the endpoints.
 * If the headings are the same, it will return a constant interpolator.
 */
fun getHeadingInterpolator(vararg points: Pose): Interpolator {
    require(points.size > 1) { PathBuilderScope.NOT_ENOUGH_POINTS_ERR_MSG }
    return getHeadingInterpolator(points.first().heading(), points.last().heading())
}
