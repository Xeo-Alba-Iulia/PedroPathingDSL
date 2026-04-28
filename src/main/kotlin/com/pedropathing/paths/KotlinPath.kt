package com.pedropathing.paths

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose

@PathMarker
class KotlinPath @PublishedApi internal constructor() {
    @PublishedApi internal val pathPoints = mutableListOf<Pose>()
    @PublishedApi internal val callbacks = mutableListOf<CallbackFactory>()

    operator fun Pose.unaryPlus() { pathPoints += this }

    fun callbacks(builder: CallbackBuilder.() -> Unit) {
        val callbackBuilder = CallbackBuilder()
        callbackBuilder.builder()
        callbacks += callbackBuilder.callbacks
    }

    @PublishedApi internal inline fun build(curveFactory: (List<Pose>) -> Curve) = Pair(
        Path(
            when (pathPoints.size) {
                0 -> throw IllegalStateException("A path must have at least one point")
                1 -> BezierPoint(pathPoints.first())
                2 -> BezierLine(pathPoints.first(), pathPoints.last())
                else -> curveFactory(pathPoints)
            }
        ), callbacks)
}