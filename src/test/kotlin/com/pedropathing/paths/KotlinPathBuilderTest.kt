package com.pedropathing.paths

import com.pedropathing.follower.Follower
import com.pedropathing.geometry.Pose
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.atan2
import kotlin.time.Duration.Companion.milliseconds

class KotlinPathBuilderTest {
    val follower = mockk<Follower>().apply {
        every { constraints } returns PathConstraints(0.0, 0.0)
    }

    @Test
    fun pathFacingPoint() {
        val path = follower.pathChain {
            pathFacingPoint(Pose(1.0, 1.0), Pose(0.0, 0.0), Pose(2.0, 0.0))
            pathFacingPoint(1.0, 1.0, Pose(0.0, 0.0), Pose(2.0, 0.0))
        }

        val pathPoint = path.firstPath().getClosestPoint(Pose(0.5, 0.0), 20, 0.5)

        val firstHeadingInterpolator = path.firstPath().headingInterpolator
        val secondHeadingInterpolator = path.lastPath().headingInterpolator

        assertEquals(atan2(1.0, 0.5), HeadingInterpolator.facingPoint(Pose(1.0, 1.0)).interpolate(pathPoint), 1e-3)

        assertEquals(
            firstHeadingInterpolator.interpolate(pathPoint),
            HeadingInterpolator.facingPoint(Pose(1.0, 1.0)).interpolate(pathPoint)
        )
        assertEquals(
            secondHeadingInterpolator.interpolate(pathPoint),
            HeadingInterpolator.facingPoint(Pose(1.0, 1.0)).interpolate(pathPoint)
        )
    }

    @Test
    fun pathChainExample() {
        follower.pathChain {
            path(Pose(0.0, 0.0), Pose(1.0, 1.0), Pose(2.0, 0.0)) {
                temporalCallback(500.milliseconds) {
                    println("Reached half a second on this path!")
                }
            }
         }
    }
}