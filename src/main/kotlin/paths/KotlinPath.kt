package com.pedropathing.paths

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.BezierPoint
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose

@PathMarker
class KotlinPath internal constructor(
    private val curveFactory: (List<Pose>) -> Curve,
) {
    private val pathPoints = mutableListOf<Pose>()
    private val callbacks = mutableListOf<CallbackFactory>()

    operator fun Pose.unaryPlus() { pathPoints += this }

    fun callbacks(builder: CallbackBuilder.() -> Unit) {
        val callbackBuilder = CallbackBuilder()
        callbackBuilder.builder()
        callbacks += callbackBuilder.build()
    }

    internal fun build() = Pair(Path(when (pathPoints.size) {
        0 -> throw IllegalStateException("A path must have at least one point")
        1 -> BezierPoint(pathPoints.first())
        2 -> BezierLine(pathPoints.first(), pathPoints.last())
        else -> curveFactory(pathPoints)
    }), callbacks)
}