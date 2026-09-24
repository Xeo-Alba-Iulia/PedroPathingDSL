package com.pedropathing.paths.callbacks

import com.pedropathing.follower.Follower

interface Callback {
    fun shouldRun(follower: Follower): Boolean
    fun callback()
}
