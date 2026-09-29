package com.pedropathing.callbacks

import com.pedropathing.follower.Follower
import com.pedropathing.paths.curves.Curve
import java.util.WeakHashMap

interface Callback {
    fun shouldRun(follower: Follower): Boolean
    fun callback()
}

@InternalCallbacksApi val callbacksMap = WeakHashMap<Curve, MutableList<Callback>>()
