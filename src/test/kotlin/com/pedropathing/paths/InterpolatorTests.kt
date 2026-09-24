package com.pedropathing.paths

import com.pedropathing.api.Paths
import com.pedropathing.math.Pose
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.test.ExperimentalKotlinTestApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterpolatorTests {
    @OptIn(ExperimentalKotlinTestApi::class)
    @Test
    fun testTangent() {
        val startPose = Pose.zero()
        val endPose = Pose(10.0, 5.0, 90.0)
        val path = path {
            tangent(startPose, endPose)
        }

        val expectedHeading = atan2(endPose.y() - startPose.y(), endPose.x() - startPose.x())
        for (i in 0..100 step 30) {
            val ratio = i / 100.0
            val expectedPose = Pose.interpolate(startPose, endPose, ratio).withHeading(expectedHeading)
            val actualPose = path[ratio]
            assertTrue(poseEquals(expectedPose, actualPose)) {
                buildString {
                    appendLine("Expected: $expectedPose")
                    appendLine("Actual: ${path[ratio]}")
                    appendLine("Ratio: $ratio")
                }
            }
        }
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
            assertTrue(poseEquals(path[i / 100.0], pedroPath[i / 100.0]))
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
            assertEquals(endPose.heading() * ratio, path.heading(ratio), 0.001)
        }
    }
}

fun poseEquals(a: Pose, b: Pose) = a.x() == b.x() && a.y() == b.y() && a.heading() == b.heading()
