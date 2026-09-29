package com.pedropathing.callbacks

import com.pedropathing.follower.Follower
import com.pedropathing.paths.TValue

class ParametricCallback(val t: Double, val callback: () -> Unit) : Callback {
    init {
        TValue.check(t)
    }

    override fun shouldRun(follower: Follower): Boolean =
        follower.currentCurve().closestParameter(follower.pose().toVector2D()) >= t

    override fun callback() = callback.invoke()
}