package com.pedropathing.api

import com.pedropathing.follower.Follower
import com.pedropathing.math.Pose
import com.pedropathing.callbacks.Callback
import com.pedropathing.callbacks.ParametricCallback
import com.pedropathing.callbacks.TemporalCallback
import com.pedropathing.paths.curves.Curve
import java.util.LinkedList
import kotlin.time.Duration

@PathMarker
class CallbackBuilderScope @PublishedApi internal constructor(val curve: Curve) {
    /**
     * Adds a callback to be executed during the path-following process.
     *
     * @param callback The callback object to be added.
     */
    fun callback(callback: Callback) { this.callbacks += callback }

    /**
     * Adds a callback to be executed during the path-following process.
     *
     * @param isReady A function that determines whether the callback should be executed.
     * @param callback The callback function to be executed.
     */
    fun callback(isReady: () -> Boolean, callback: () -> Unit) =
        callback(
            object : Callback {
                override fun shouldRun(follower: Follower) = isReady()
                override fun callback() = callback.invoke()
            }
        )

    /**
     * Adds a callback that will be executed after a specified duration.
     *
     * @param time The duration after which the callback should be executed.
     * @param callback The callback function to be executed.
     */
    fun temporalCallback(time: Duration, callback: () -> Unit) =
        callback(TemporalCallback(time, callback))

    /**
     * Adds a parametric callback to be executed during the path-following process.
     *
     * @param t The parametric position along the curve where the callback should be triggered.
     *          This value must be within the range [0, 1], where 0 corresponds to the start
     *          of the curve and 1 corresponds to the end of the curve.
     *          *Note: No guarantees are made regarding execution of callbacks when [t] is very close to 1*
     * @param callback The callback function to be executed when the specified parametric position is reached.
     */
    fun parametricCallback(t: Double, callback: () -> Unit) =
        callback(ParametricCallback(t, callback))

    /**
     * Adds a callback that will be executed at the closest point on the curve to the given [pose].
     *
     * @param pose The pose for which the closest point on the curve is to be found.
     *             The function will calculate the closest parametric value on the curve to this pose.
     */
    fun poseCallback(pose: Pose, callback: () -> Unit) =
        parametricCallback(curve.closestParameter(pose.toVector2D()), callback)

    @PublishedApi internal val callbacks: MutableList<Callback> = LinkedList<Callback>()
    @PublishedApi internal fun build(): MutableList<Callback> = callbacks
}