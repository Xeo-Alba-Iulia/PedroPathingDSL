package com.pedropathing.paths

import com.pedropathing.follower.Follower

data class Callback(
    var hasRan: Boolean = false,
    val condition: (Follower) -> Boolean,
    val callback: () -> Unit,
)
