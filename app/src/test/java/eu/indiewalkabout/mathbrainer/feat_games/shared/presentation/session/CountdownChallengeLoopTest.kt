package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountdownChallengeLoopTest {

    @Test
    fun `countdown emits ticks and expiration`() = runTest {
        val ticks = mutableListOf<Long>()
        var expired = false
        val loop = CountdownChallengeLoop(
            scope = backgroundScope,
            countdownStepMs = 1_000L,
            nextChallengeDelayMs = 500L
        )

        loop.startCountdown(
            durationMs = 3_000L,
            onTick = { ticks += it },
            onExpired = { expired = true }
        )

        advanceTimeBy(3_000L)
        runCurrent()

        assertEquals(listOf(2_000L, 1_000L, 0L), ticks)
        assertTrue(expired)
    }

    @Test
    fun `delayed launch runs only when condition stays true`() = runTest {
        var launches = 0
        val loop = CountdownChallengeLoop(
            scope = backgroundScope,
            countdownStepMs = 1_000L,
            nextChallengeDelayMs = 500L
        )

        loop.scheduleNextChallenge(
            shouldLaunch = { true },
            onLaunch = { launches += 1 }
        )
        advanceTimeBy(500L)
        runCurrent()

        loop.scheduleNextChallenge(
            shouldLaunch = { false },
            onLaunch = { launches += 1 }
        )
        advanceTimeBy(500L)
        runCurrent()

        assertEquals(1, launches)
    }
}
