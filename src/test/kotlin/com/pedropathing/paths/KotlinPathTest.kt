package com.pedropathing.paths

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.Pose
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertIsNot

class KotlinPathTest {
    private class CustomTestCurve(
        points: List<Pose>,
    ) : BezierCurve(points)

    @Test
    fun `Empty pose list throws exception`() {
        val path = KotlinPath(::BezierCurve)
        assertFailsWith(IllegalStateException::class, path::build)
    }

    @Test
    fun `Custom curve factory is used`() {
        val testPathChain = pathChain(follower = null) {
            path {
                +Pose(0.0, 0.0, 0.0)
                +Pose(1.0, 1.0, 0.0)
                +Pose(2.0, 0.0, 0.0)
            }
            path(curveFactory = ::CustomTestCurve) {
                +Pose(0.0, 0.0, 0.0)
                +Pose(1.0, 1.0, 0.0)
                +Pose(2.0, 0.0, 0.0)
            }
        }

        val firstCurve = testPathChain.firstPath().curve
        val secondCurve = testPathChain.lastPath().curve

        assertIs<BezierCurve>(firstCurve)
        assertIsNot<CustomTestCurve>(firstCurve, "First curve shouldn't be a custom Curve")
        assertIs<CustomTestCurve>(secondCurve, "Second curve should be a custom Curve")
    }
}