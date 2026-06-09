package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameSessionTrackerTest {

    @Test
    fun `load stats exposes saved high score and persistence payload`() = runTest {
        val tracker = GameSessionTracker(defaultGameId = "memory_flash")

        tracker.loadStats("memory_flash", 0) {
            GameStats(
                gameId = "memory_flash",
                highScore = 42,
                challengesPlayed = 5,
                challengesWon = 4,
                challengesLost = 1,
                lastLevel = 3
            )
        }

        tracker.beginSession()

        assertEquals(42, tracker.highScore)
        assertEquals(42, tracker.sessionHighScoreOr())

        val request = tracker.buildPersistRequest(finalScore = 50, currentLevel = 4)
        assertNotNull(request)
        assertEquals(42, request!!.previousStats?.highScore)
        assertEquals(50, request.updatedStats.highScore)
        assertEquals(4, request.updatedStats.lastLevel)
    }

    @Test
    fun `persistence request is emitted only once per session`() = runTest {
        val tracker = GameSessionTracker(defaultGameId = "enigma")

        tracker.loadStats("enigma", 0) { null }
        tracker.beginSession()
        tracker.recordSuccess(newScore = 50, levelReached = 2)

        val first = tracker.buildPersistRequest(finalScore = 50, currentLevel = 2)
        val second = tracker.buildPersistRequest(finalScore = 50, currentLevel = 2)

        assertNotNull(first)
        assertNull(second)
    }
}
