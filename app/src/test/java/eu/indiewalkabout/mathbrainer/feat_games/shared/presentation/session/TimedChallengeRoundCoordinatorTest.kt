package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedChallengeRoundCoordinatorTest {

    @Test
    fun `success result updates score high score and level progression`() {
        val sessionTracker = GameSessionTracker(defaultGameId = "math_write")
        sessionTracker.beginSession()
        val progression = ArithmeticChallengeProgression(
            initialChallengesPerLevel = 1,
            challengesPerLevelIncrement = 2,
            promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.ON_TARGET,
            timerIncrementProvider = { 1_000L }
        )
        progression.reset()
        val coordinator = TimedChallengeRoundCoordinator(
            scoreIncrement = 25,
            progression = progression,
            sessionTracker = sessionTracker
        )

        val result = coordinator.onSuccess(currentScore = 50, currentLevel = 1)

        assertEquals(75, result.newScore)
        assertEquals(75, result.highScore)
        assertEquals(2, result.level)
        assertTrue(result.didPromoteLevel)
        assertEquals(0, result.challengesCompleted)
        assertEquals(3, result.challengesPerLevel)
    }

    @Test
    fun `failure result decrements lives and flags game over when exhausted`() {
        val sessionTracker = GameSessionTracker(defaultGameId = "double_number")
        sessionTracker.beginSession()
        val progression = ArithmeticChallengeProgression(
            initialChallengesPerLevel = 10,
            challengesPerLevelIncrement = 0,
            promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.ON_TARGET,
            timerIncrementProvider = { 1_000L }
        )
        val coordinator = TimedChallengeRoundCoordinator(
            scoreIncrement = 25,
            progression = progression,
            sessionTracker = sessionTracker
        )

        val safeFailure = coordinator.onFailure(currentLives = 3, currentLevel = 1)
        val gameOverFailure = coordinator.onFailure(currentLives = 1, currentLevel = 1)

        assertEquals(2, safeFailure.remainingLives)
        assertFalse(safeFailure.isGameOver)
        assertEquals(0, gameOverFailure.remainingLives)
        assertTrue(gameOverFailure.isGameOver)
    }
}
