package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState

class ArithmeticChallengeProgression(
    private val initialChallengesPerLevel: Int,
    private val challengesPerLevelIncrement: Int,
    private val promotionThreshold: PromotionThreshold,
    private val timerIncrementProvider: (Int) -> Long,
    private val optionsConfig: OptionsConfig? = null,
    private val multiplicationConfig: OperationBoundsConfig? = null,
    private val divisionConfig: OperationBoundsConfig? = null
) {
    var operandRangeMin: Int = INITIAL_OPERAND_RANGE_MIN
        private set
    var operandRangeMax: Int = INITIAL_OPERAND_RANGE_MAX
        private set
    var timerLength: Long = ChallengeUiState.INITIAL_TIMER_LENGTH
        private set
    var challengesPerLevel: Int = initialChallengesPerLevel
        private set
    var challengesCompleted: Int = 0
        private set
    var optionsCount: Int? = optionsConfig?.initialCount
        private set
    val currentMultiplicationConfig: OperationBoundsConfig?
        get() = multiplicationConfig
    val currentDivisionConfig: OperationBoundsConfig?
        get() = divisionConfig

    fun reset() {
        operandRangeMin = INITIAL_OPERAND_RANGE_MIN
        operandRangeMax = INITIAL_OPERAND_RANGE_MAX
        timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
        challengesPerLevel = initialChallengesPerLevel
        challengesCompleted = 0
        optionsCount = optionsConfig?.initialCount
        multiplicationConfig?.reset()
        divisionConfig?.reset()
    }

    fun recordSuccess(currentLevel: Int): ProgressionUpdate {
        challengesCompleted++
        var nextLevel: Int? = null

        if (shouldPromote()) {
            challengesCompleted = 0
            nextLevel = currentLevel + 1
            applyPromotion(nextLevel)
        }

        return ProgressionUpdate(
            nextLevel = nextLevel,
            timerLength = timerLength,
            challengesPerLevel = challengesPerLevel,
            challengesCompleted = challengesCompleted,
            optionsCount = optionsCount
        )
    }

    private fun shouldPromote(): Boolean {
        return when (promotionThreshold) {
            PromotionThreshold.ON_TARGET -> challengesCompleted >= challengesPerLevel
            PromotionThreshold.AFTER_TARGET -> challengesCompleted > challengesPerLevel
        }
    }

    private fun applyPromotion(level: Int) {
        operandRangeMin = operandRangeMax
        operandRangeMax = 100 * level + 50 * (level - 1)
        timerLength += timerIncrementProvider(level)
        challengesPerLevel += challengesPerLevelIncrement
        multiplicationConfig?.increment()
        divisionConfig?.increment()
        optionsCount = optionsConfig?.increment(optionsCount)
    }

    data class ProgressionUpdate(
        val nextLevel: Int?,
        val timerLength: Long,
        val challengesPerLevel: Int,
        val challengesCompleted: Int,
        val optionsCount: Int?
    )

    enum class PromotionThreshold {
        ON_TARGET,
        AFTER_TARGET
    }

    data class OptionsConfig(
        val initialCount: Int,
        val maxCount: Int
    ) {
        fun increment(currentCount: Int?): Int {
            val safeCount = currentCount ?: initialCount
            return (safeCount + 1).coerceAtMost(maxCount)
        }
    }

    class OperationBoundsConfig(
        initialLowMax: Int,
        initialHighMax: Int,
        private val lowIncrement: Int,
        private val highIncrement: Int
    ) {
        private val baseLowMax = initialLowMax
        private val baseHighMax = initialHighMax

        var lowMax: Int = initialLowMax
            private set
        var highMax: Int = initialHighMax
            private set

        fun reset() {
            lowMax = baseLowMax
            highMax = baseHighMax
        }

        fun increment() {
            lowMax += lowIncrement
            highMax += highIncrement
        }
    }

    companion object {
        private const val INITIAL_OPERAND_RANGE_MIN = 1
        private const val INITIAL_OPERAND_RANGE_MAX = 100
    }
}
