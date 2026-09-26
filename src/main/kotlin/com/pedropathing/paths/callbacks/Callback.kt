package com.pedropathing.paths.callbacks

import com.pedropathing.follower.Follower
import com.pedropathing.paths.InternalCallbacksApi
import com.pedropathing.paths.curves.Curve
import java.util.WeakHashMap

interface Callback {
    fun shouldRun(follower: Follower): Boolean
    fun callback()
}

@InternalCallbacksApi val callbacks = WeakHashMap<Curve, MutableList<Callback>>()
