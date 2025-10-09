package com.pedropathing.paths

import com.pedropathing.geometry.Pose
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.atan2

class KotlinPathBuilderTest {
    @Test
    fun pathFacingPoint() {
        val path = pathChain(null) {
            pathFacingPoint(Pose(1.0, 1.0)) {
                +Pose(0.0, 0.0)
                +Pose(2.0, 0.0)
            }
            pathFacingPoint(1.0, 1.0) {
                +Pose(0.0, 0.0)
                +Pose(2.0, 0.0)
            }
            build()
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
}