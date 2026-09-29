package com.pedropathing

import com.pedropathing.api.Paths
import com.pedropathing.api.path
import com.pedropathing.math.Pose
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.math.PI
import kotlin.math.atan2

class InterpolatorTests {
    @ParameterizedTest
    @ValueSource(doubles = [0.0, 0.25, 0.3, 0.5, 0.75, 1.0])
    fun testTangent(ratio: Double) {
        val startPose = Pose.zero()
        val endPose = Pose(10.0, 5.0, 90.0)
        val path = path {
            tangent(startPose, endPose)
        }

        val expectedHeading = atan2(endPose.y() - startPose.y(), endPose.x() - startPose.x())
        val expectedPose = Pose.interpolate(startPose, endPose, ratio).withHeading(expectedHeading)
        val actualPose = path[ratio]
        assertPoseEquals(expectedPose, actualPose)
    }

    @Test
    fun testTangentCurve() {
        val startPose = Pose.zero()
        val middlePose = Pose(0.0, 10.0, 0.0)
        val endPose = Pose(10.0, 5.0, 0.0)
        val pedroPath = Paths.curve(startPose, middlePose, endPose).tangent()
        val path = path {
            tangent(startPose, middlePose, endPose)
        }

        for (i in 0..100 step 5) {
            assertPoseEquals(path[i / 100.0], pedroPath[i / 100.0])
        }
    }

    @Test
    fun testLinear() {
        val startPose = Pose.zero()
        val endPose = Pose(10.0, 5.0, PI / 2)
        val path = path {
            linear(startPose, endPose)
        }
        for (i in 0..100 step 10) {
            val ratio = i / 100.0
            Assertions.assertEquals(endPose.heading() * ratio, path.heading(ratio), 0.001)
        }
    }
    fun assertPoseEquals(expectedPose: Pose, actualPose: Pose) = assertAll(
        "Pose properties",
        { Assertions.assertEquals(expectedPose.x(), actualPose.x(), "x") },
        { Assertions.assertEquals(expectedPose.y(), actualPose.y(), "y") },
        { Assertions.assertEquals(expectedPose.heading(), actualPose.heading(), "heading") },
    )
}