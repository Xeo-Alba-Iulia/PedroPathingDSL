package com.pedropathing.callbacks

import com.pedropathing.follower.Follower
import com.pedropathing.ivy.Command
import com.pedropathing.ivy.behaviors.BlockedBehavior
import com.pedropathing.ivy.behaviors.ConflictBehavior
import com.pedropathing.ivy.behaviors.EndCondition
import com.pedropathing.ivy.behaviors.InterruptedBehavior
import com.pedropathing.paths.curves.Curve
import com.qualcomm.robotcore.util.RobotLog

class CallbackRunner(private val follower: Follower) : Command {
    private var lastCurve: Curve? = null

    override fun requirements() = setOf(this, callbackLock)
    override fun priority() = 0
    override fun interruptedBehavior() = InterruptedBehavior.SUSPEND
    override fun conflictBehavior() = ConflictBehavior.QUEUE
    override fun blockedBehavior() = BlockedBehavior.QUEUE
    override fun start() {}
    override fun done() = false
    override fun execute() {
        val curve = follower.currentCurve()
        if (lastCurve != null && lastCurve != curve) callbacksMap -= lastCurve
        lastCurve = curve
        val currentCallbackList = callbacksMap[curve] ?: return
        val iter = currentCallbackList.iterator()
        while(iter.hasNext()) {
            val current = iter.next()
            if (current.shouldRun(follower)) {
                current.callback()
                iter.remove()
            }
        }
        if (currentCallbackList.isEmpty()) callbacksMap -= curve
    }
    override fun end(condition: EndCondition) {
        if (condition == EndCondition.SUSPENDED) RobotLog.ii(TAG, "CallbackRunner was suspended")
    }

    companion object {
        const val TAG = "PedroPathingDSL"
    }
}

private val callbackLock = Any()
