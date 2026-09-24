package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.math.Pose
import com.pedropathing.paths.callbacks.Callback
import com.pedropathing.paths.callbacks.ParametricCallback
import com.pedropathing.paths.callbacks.TemporalCallback
import com.pedropathing.paths.curves.Curve
import java.util.LinkedList
import kotlin.time.Duration

@PathMarker
class CallbackBuilderScope @PublishedApi internal constructor(val curve: Curve) {
    fun addCallback(callback: Callback) { this.callbacks += callback }
    fun addCallback(isReady: () -> Boolean, callback: () -> Unit) =
        addCallback(
            object : Callback {
                override fun shouldRun(follower: Follower) = isReady()
                override fun callback() = callback.invoke()
            }
        )

    fun temporalCallback(time: Duration, callback: () -> Unit) =
        addCallback(TemporalCallback(time, callback))
    fun parametricCallback(t: Double, callback: () -> Unit) =
        addCallback(ParametricCallback(t, callback))
    fun poseCallback(pose: Pose, callback: () -> Unit) =
        parametricCallback(curve.closestParameter(pose.toVector2D()), callback)

    private val callbacks: MutableList<Callback> = LinkedList<Callback>()
    fun build(): MutableList<Callback> = callbacks
}