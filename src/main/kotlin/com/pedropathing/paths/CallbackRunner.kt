package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.paths.callbacks.Callback
import com.pedropathing.paths.curves.Curve
import java.util.WeakHashMap

object CallbackRunner {
    val callbacks = WeakHashMap<Curve, MutableList<Callback>>()

    fun update(follower: Follower) {
        val curve = follower.currentCurve()
        val callbacks = callbacks[curve] ?: return
        val iter = callbacks.iterator()
        while(iter.hasNext()) {
            val current = iter.next()
            if (current.shouldRun(follower)) {
                current.callback()
                iter.remove()
            }
        }
        if (callbacks.isEmpty()) this.callbacks -= curve
    }
}