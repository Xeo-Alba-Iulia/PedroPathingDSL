package com.pedropathing.paths

import com.pedropathing.config.Modifier
import com.pedropathing.math.Pose
import com.pedropathing.paths.interpolator.Interpolator
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PathBuilderTest {
    @Test
    fun simplePathTest() {
        path {
            linear(Pose(0.0, 0.0, 0.0), Pose(1.0, 1.0, 0.0))
            tangent(Pose(1.0, 1.0, 0.0), Pose(2.0, 2.0, 0.0))
            constant(Pose(2.0, 2.0, 45.0), Pose(10.0, 10.0, 45.0))
        }
    }

    @Test
    fun pathFailsOnLessThanTwoPoints() {
        val emptyArray = arrayOf<Pose>()
        val singlePose = arrayOf(Pose(0.0, 0.0, 0.0))
        nonEmptyPath {
            assertFailsWith<IllegalArgumentException> { linear(*emptyArray) }
            assertFailsWith<IllegalArgumentException> { linear(*singlePose) }
        }
    }

    @Test
    fun pathFailsOnNonMatchingHeadings() {
        val nonMatchingHeadings = arrayOf(Pose(0.0, 0.0, 0.0), Pose(1.0, 1.0, 90.0))
        nonEmptyPath {
            assertFailsWith<IllegalArgumentException> { constant(*nonMatchingHeadings) }
        }
    }

    @Test
    fun pathFailsOnEmptyPath() {
        assertFailsWith<IllegalStateException> { path {} }
    }
}

fun nonEmptyPath(
    interpolator: Interpolator? = null,
    modifiers: List<Modifier> = emptyList(),
    block: PathBuilderScope.() -> Unit
) = PathBuilderScope().apply {
    tangent(Pose(0.0, 0.0, 0.0), Pose(1.0, 1.0, 0.0))
    block()
}