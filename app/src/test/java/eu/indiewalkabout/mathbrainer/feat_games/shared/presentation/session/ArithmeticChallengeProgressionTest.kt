package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArithmeticChallengeProgressionTest {

    @Test
    fun `after target promotion waits until challenge count exceeds level target`() {
        val progression = ArithmeticChallengeProgression(
            initialChallengesPerLevel = 2,
            challengesPerLevelIncrement = 2,
            promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.AFTER_TARGET,
            timerIncrementProvider = { level -> level * 1_000L }
        )

        progression.reset()

        val first = progression.recordSuccess(currentLevel = 1)
        val second = progression.recordSuccess(currentLevel = 1)
        val third = progression.recordSuccess(currentLevel = 1)

        assertNull(first.nextLevel)
        assertNull(second.nextLevel)
        assertEquals(2, second.challengesCompleted)
        assertEquals(2, second.challengesPerLevel)
        assertEquals(2, third.nextLevel)
        assertEquals(0, third.challengesCompleted)
        assertEquals(4, third.challengesPerLevel)
        assertEquals(100, progression.operandRangeMin)
        assertEquals(250, progression.operandRangeMax)
        assertEquals(ChallengeUiState.INITIAL_TIMER_LENGTH + 2_000L, third.timerLength)
    }

    @Test
    fun `on target promotion advances as soon as challenge count reaches level target`() {
        val progression = ArithmeticChallengeProgression(
            initialChallengesPerLevel = 2,
            challengesPerLevelIncrement = 0,
            promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.ON_TARGET,
            timerIncrementProvider = { 1_000L }
        )

        progression.reset()

        val first = progression.recordSuccess(currentLevel = 1)
        val second = progression.recordSuccess(currentLevel = 1)

        assertNull(first.nextLevel)
        assertEquals(2, second.nextLevel)
        assertEquals(0, second.challengesCompleted)
        assertEquals(100, progression.operandRangeMin)
        assertEquals(250, progression.operandRangeMax)
        assertEquals(ChallengeUiState.INITIAL_TIMER_LENGTH + 1_000L, second.timerLength)
    }

    @Test
    fun `promotion increments operation bounds and option count when configured`() {
        val progression = ArithmeticChallengeProgression(
            initialChallengesPerLevel = 1,
            challengesPerLevelIncrement = 2,
            promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.ON_TARGET,
            timerIncrementProvider = { 1_500L },
            optionsConfig = ArithmeticChallengeProgression.OptionsConfig(
                initialCount = 3,
                maxCount = 4
            ),
            multiplicationConfig = ArithmeticChallengeProgression.OperationBoundsConfig(
                initialLowMax = 15,
                initialHighMax = 30,
                lowIncrement = 1,
                highIncrement = 5
            ),
            divisionConfig = ArithmeticChallengeProgression.OperationBoundsConfig(
                initialLowMax = 11,
                initialHighMax = 15,
                lowIncrement = 1,
                highIncrement = 2
            )
        )

        progression.reset()
        val update = progression.recordSuccess(currentLevel = 1)

        assertEquals(2, update.nextLevel)
        assertEquals(4, update.optionsCount)
        assertEquals(16, progression.currentMultiplicationConfig?.lowMax)
        assertEquals(35, progression.currentMultiplicationConfig?.highMax)
        assertEquals(12, progression.currentDivisionConfig?.lowMax)
        assertEquals(17, progression.currentDivisionConfig?.highMax)
        assertEquals(3, update.challengesPerLevel)
        assertEquals(ChallengeUiState.INITIAL_TIMER_LENGTH + 1_500L, update.timerLength)
    }
}
