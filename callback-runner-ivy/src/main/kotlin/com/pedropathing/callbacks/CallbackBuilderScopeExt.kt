package com.pedropathing.callbacks

import com.pedropathing.api.CallbackBuilderScope
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.commands.Commands
import com.pedropathing.math.Pose
import kotlin.time.Duration

/**
 * Extension function that schedules a [Command] to be executed after a specified [time] duration has elapsed
 * since the follower started following the path.
 *
 * @param time The duration after which the command should be executed.
 * @param command The command to be scheduled.
 */
fun CallbackBuilderScope.temporalCallback(time: Duration, command: Command) =
    temporalCallback(time, command::schedule)

/**
 * Extension function that schedules a [Command] to be executed when the follower reaches a specified parametric position
 * on the curve.
 *
 * @param t The parametric position along the curve where the command should be triggered.
 *          This value must be within the range [0, 1], where 0 corresponds to the start
 *          of the curve and 1 corresponds to the end of the curve.
 *          *Note: No guarantees are made regarding execution of callbacks when [t] is very close to 1*
 * @param command The command to be scheduled.
 */
fun CallbackBuilderScope.parametricCallback(t: Double, command: Command) =
    parametricCallback(t, command::schedule)

/**
 * Extension function that schedules a [Command] to be executed at the closest point on the curve to a specified [pose].
 *
 * @param pose The pose for which the closest point on the curve is to be found.
 *             The function will calculate the closest parametric value on the curve to this pose.
 *             The command will be scheduled at this parametric position.
 * @param command The command to be scheduled.
 */
fun CallbackBuilderScope.poseCallback(pose: Pose, command: Command) =
    poseCallback(pose, command::schedule)