package com.pedropathing.paths

import com.pedropathing.api.Paths
import com.pedropathing.math.Pose
import com.pedropathing.paths.curves.Curve
import com.pedropathing.paths.interpolator.Interpolator

@PathMarker
class PathBuilderScope @PublishedApi internal constructor() {
    inline fun path(
        vararg points: Pose,
        interpolator: Interpolator = Interpolator.tangent,
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        val path = Paths.path(createCurve(points)).heading(interpolator)
        paths += path
        addCallbacks(path.curve, block)
    }

    inline fun linearPath(
        vararg points: Pose,
        startHeading: Double,
        endHeading: Double,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.linear(startHeading, endHeading), block = block)

    inline fun linearPath(
        vararg points: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = linearPath(
        *points,
        startHeading = points.first().heading(),
        endHeading = points.last().heading(),
        block = block
    )

    inline fun constantPath(
        vararg points: Pose,
        heading: Double,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.constant(heading), block = block)

    inline fun constantPath(
        vararg points: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) {
        require(points.first().heading() == points.last().heading()) { "End points must have the same heading" }
        return constantPath(*points, heading = points.first().heading(), block = block)
    }

    inline fun pathFacingPoint(
        vararg points: Pose,
        target: Pose,
        block: CallbackBuilderScope.() -> Unit = {}
    ) = path(*points, interpolator = Interpolator.facingPoint(target), block = block)

    inline fun pathToPoint(
        endPoint: Pose,
        interpolator: Interpolator? = null,
        block: CallbackBuilderScope.() -> Unit = {}
    ) =
        paths
            .lastOrNull()
            ?.let { path ->
                val startPoint = path.curve.endPoint().toPose()
                val startHeading = startPoint.heading()
                val endHeading = endPoint.heading()
                val interpolator = when {
                    interpolator != null -> interpolator
                    startHeading == endHeading -> Interpolator.constant(startHeading)
                    else -> Interpolator.linear(startHeading, endHeading)
                }

                path(
                    startPoint, endPoint,
                    interpolator = interpolator,
                    block = block
                )
            } ?: throw IllegalStateException("No paths have been created yet")

    @PublishedApi internal val paths = mutableListOf<Path>()
    @PublishedApi internal val callbacks = mutableMapOf<Curve, List<Callback>>()

    @PublishedApi internal inline fun addCallbacks(curve: Curve, block: CallbackBuilderScope.() -> Unit) {
        CallbackBuilderScope(curve)
            .apply(block)
            .build()
            .takeIf { it.isNotEmpty() }
            ?.let { this.callbacks += curve to it }
    }
}
