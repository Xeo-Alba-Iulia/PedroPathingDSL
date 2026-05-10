package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Curve
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.callbacks.ParametricCallback
import com.pedropathing.paths.callbacks.PathCallback
import com.pedropathing.paths.callbacks.PoseCallback
import com.pedropathing.paths.callbacks.TemporalCallback
import com.pedropathing.util.FiniteRunAction
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit

@PathMarker
class CallbackBuilderKt internal constructor(
    private val follower: Follower,
    val pathIndex: Int,
    val curve: Curve,
) {
    internal val callbacks = LinkedList<PathCallback>()

    fun addCallback(callback: PathCallback, timesToRun: Int = 1) {
        callbacks.add(FiniteRunAction(callback, timesToRun))
    }

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

    private class PathCallbackImpl(
        val pathIndex: Int,
        val isReady: () -> Boolean,
        val callback: () -> Boolean,
    ) : PathCallback {
        override fun run() = callback()
        override fun isReady() = isReady.invoke()
        override fun getPathIndex() = pathIndex
    }

    /**
     * Adds a callback to the path that runs the given [callback] function when [isReady] returns true.
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
        callbacks.add(PathCallbackImpl(pathIndex, isReady, callback))
    }

    /**
     * Adds a callback that executes at the given [duration] after the path start.
     *
     * @param duration The duration to wait since the path start to execute [callback]
     */
    fun temporalCallback(duration: Duration, callback: () -> Unit) =
        addCallback(TemporalCallback(pathIndex, duration.toDouble(DurationUnit.MILLISECONDS), callback))

    /**
     * Adds a callback that executes after a given percent of the path is completed.
     *
     * @param parametricValue A value in the range 0..1, representing the percent completion of the path
     *                        at which the callback fires.
     * @param callback The function to run at the given [parametricValue] along the path.
     */
    fun parametricCallback(parametricValue: Double, callback: () -> Unit) =
        addCallback(
            ParametricCallback(
                pathIndex,
                parametricValue,
                follower,
                callback
            )
        )

    /**
     * Adds a callback that fires when the robot reaches the specified [targetPose] on the path.
     *
     * *This function binary searches for the t-value where the given [targetPose] lies on the path.*
     *
     * @param targetPose The pose on the path at which to trigger the callback.
     * @param callback The function to run at the given [targetPose] along the path.
     * @param initialGuess A guess of the t-value corresponding to the [targetPose]
     */
    fun positionCallback(targetPose: Pose, initialGuess: Double = 0.5, callback: () -> Unit) =
        addCallback(
            PoseCallback(
                follower,
                pathIndex,
                targetPose,
                callback,
                initialGuess,
                curve
            )
        )
}