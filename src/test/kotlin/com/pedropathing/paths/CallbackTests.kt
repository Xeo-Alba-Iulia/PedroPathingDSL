package com.pedropathing.paths

import com.pedropathing.math.Pose
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

class CallbackTests {
    @Test
    @Disabled("This test depends on the JVM it runs on")
    fun callbackGetsGCTest() {
        for (i in 0..50) {
            val _ = path {
                constant(Pose(0.0, 0.0, 0.0), Pose(1.0, 1.0, 0.0)) {
                    temporalCallback(100.milliseconds) { println(i) }
                }
            }
            if (callbacks.size < i) return
            System.gc()
        }
        return fail("callback map should be garbage collected")
    }

    // TODO: Mock the follower to check for callback execution
}