package com.pedropathing.callbacks

import com.pedropathing.follower.Follower
import com.pedropathing.paths.curves.Curve

@ExperimentalCallbacksApi
object CallbackRunner {
    private var lastCurve: Curve? = null

    fun update(follower: Follower) {
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
}