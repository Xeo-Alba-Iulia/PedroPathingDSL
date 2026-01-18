package com.pedropathing.paths

import com.pedropathing.geometry.Pose
import com.pedropathing.paths.callbacks.ParametricCallback
import com.pedropathing.paths.callbacks.PathCallback
import com.pedropathing.paths.callbacks.PoseCallback
import com.pedropathing.paths.callbacks.TemporalCallback
import kotlin.time.Duration
import kotlin.time.DurationUnit

@PathMarker
class CallbackBuilder internal constructor() {
    private var callbacks = mutableListOf<CallbackFactory>()

    fun addCallback(callback: PathCallback) { callbacks.add { _, _, _ -> callback } }

    /**
     * Adds a callback that runs the given [callback] when [isReady] returns true.
     *
     * @param isReady A function that returns true when the callback should be run.
     * Defaults to true, meaning the callback will run when the path is reached.
     */
    fun addCallback(isReady: () -> Boolean = { true }, callback: () -> Unit) =
        addMultiCallback(isReady) {
            callback()
            true
        }

    /**
     * Adds a callback that runs the given [callback] when [isReady] returns true.
     *
     * Unlike [addCallback] this callback can return false in order to run
     * multiple times on the same [Path][com.pedropathing.paths.Path].
     *
     * @param isReady A function that returns true when the callback should be run.
     * Defaults to true, meaning the callback will run when the path is reached.
     *
     * @param callback A function that returns true if the callback should be removed after running.
     */
    fun addMultiCallback(isReady: () -> Boolean = { true }, callback: () -> Boolean) {
        callbacks.add { pathIndex, _, _ ->
            object : PathCallback {
                override fun run() = callback()
                override fun isReady() = isReady()
                override fun getPathIndex() = pathIndex
            }
        }
    }

    fun temporalCallback(time: Duration, callback: () -> Unit) {
        callbacks.add { pathIndex, _, _ ->
            TemporalCallback(pathIndex, time.toDouble(DurationUnit.MILLISECONDS), callback)
        }
    }
    fun parametricCallback(parametricValue: Double, callback: () -> Unit) {
        callbacks.add { pathIndex, follower, _ ->
            ParametricCallback(
                pathIndex,
                parametricValue,
                follower ?: throw IllegalStateException("Cannot use parametric callback without a follower"),
                callback
            )
        }
    }
    fun positionCallback(targetPose: Pose, initialGuess: Double = 0.5, callback: () -> Unit) {
        callbacks.add { pathIndex, follower, curve ->
            PoseCallback(
                follower ?: throw IllegalStateException("Cannot use position callback without a follower"),
                pathIndex,
                targetPose,
                callback,
                initialGuess,
                curve
            )
        }
    }

    internal fun build() = callbacks
}