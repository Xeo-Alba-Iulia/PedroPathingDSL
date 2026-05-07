package com.pedropathing.paths

import com.pedropathing.follower.Follower
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertFailsWith

class KotlinPathTest {
    @Test
    fun `Empty pose list throws exception`() {
        val follower = mockk<Follower>()
        every { follower.constraints } returns PathConstraints(0.0, 0.0)
        assertFailsWith<IllegalArgumentException> { follower.pathChain { path() } }
    }
}