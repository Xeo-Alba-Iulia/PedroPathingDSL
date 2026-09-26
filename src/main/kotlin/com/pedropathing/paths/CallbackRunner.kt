package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.paths.callbacks.Callback
import com.pedropathing.paths.curves.Curve
import java.util.WeakHashMap

object CallbackRunner {
    private var lastCurve: Curve? = null

    fun update(follower: Follower) {
        val curve = follower.currentCurve()
        if (lastCurve != null && lastCurve != curve) callbacks -= lastCurve
        val currentCallbackList = callbacks[curve] ?: return
        val iter = currentCallbackList.iterator()
        while(iter.hasNext()) {
            val current = iter.next()
            if (current.shouldRun(follower)) {
                current.callback()
                iter.remove()
            }
        }
        if (currentCallbackList.isEmpty()) callbacks -= curve
    }
}

@InternalCallbacksApi val callbacks = WeakHashMap<Curve, MutableList<Callback>>()
