package com.pedropathing.paths.callbacks

import com.pedropathing.follower.Follower
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class TemporalCallback(val time: Duration, val callback: () -> Unit) : Callback {
    private var mark: TimeMark? = null
    override fun shouldRun(follower: Follower): Boolean {
        if (mark == null) mark = TimeSource.Monotonic.markNow()
        return mark!!.elapsedNow() >= time
    }

    override fun callback() = callback.invoke()
}