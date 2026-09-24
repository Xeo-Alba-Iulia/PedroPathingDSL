package com.pedropathing.paths

import com.pedropathing.config.Modifier
import com.pedropathing.math.Pose
import com.pedropathing.paths.PathBuilderScope.Companion.NOT_ENOUGH_POINTS_ERR_MSG
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.curves.Line
import com.pedropathing.paths.curves.bezier.BezierCurve
import com.pedropathing.paths.interpolator.Interpolator
import org.jetbrains.annotations.Contract

@Contract("_, _, _ -> new", pure = false)
inline fun path(
    interpolator: Interpolator? = null,
    modifiers: List<Modifier> = emptyList(),
    block: PathBuilderScope.() -> Unit
): Path {
    val (path, callbacks) = PathBuilderScope().apply(block).build()
    val pathWithModifiers = applyHeadingAndModifiers(path, interpolator, modifiers)
    CallbackRunner.callbacks += callbacks
    return pathWithModifiers
}

fun applyHeadingAndModifiers(path: Path, interpolator: Interpolator?, modifiers: List<Modifier>): Path {
    val path = if (interpolator != null) path.heading(interpolator) else path
    return if (modifiers.isNotEmpty()) path.with(modifiers) else path
}

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
    require(points.size > 1) { NOT_ENOUGH_POINTS_ERR_MSG }
    return getHeadingInterpolator(points.first().heading(), points.last().heading())
}
