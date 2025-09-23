package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.callbacks.ParametricCallback
import com.pedropathing.paths.callbacks.PathCallback
import com.pedropathing.paths.callbacks.PoseCallback
import com.pedropathing.paths.callbacks.TemporalCallback
import kotlin.time.Duration

class CallbackBuilder internal constructor() {
    private val callbacks = mutableListOf<CallbackFactory>()

    fun addCallback(callback: PathCallback) = callbacks.add { _, _, _ -> callback }
    fun temporalCallback(time: Duration, callback: () -> Unit) {
        callbacks.add { pathIndex, _, _ ->
            TemporalCallback(pathIndex, time.inWholeMilliseconds.toDouble(), callback)
        }
    }
    fun parametricCallback(parametricValue: Double, follower: Follower, callback: () -> Unit) {
        callbacks.add { pathIndex, follower, _ ->
            ParametricCallback(pathIndex, parametricValue, follower, callback)
        }
    }
    fun positionCallback(targetPose: Pose, initialGuess: Double = 0.5, callback: () -> Unit) {
        callbacks.add { pathIndex, follower, curve ->
            PoseCallback(follower, pathIndex, targetPose, callback, initialGuess, curve)
        }
    }

    internal fun build() = callbacks
}