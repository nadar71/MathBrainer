package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session

class TimedChallengeRoundCoordinator(
    private val scoreIncrement: Int,
    private val progression: ArithmeticChallengeProgression,
    private val sessionTracker: GameSessionTracker
) {
    fun onSuccess(currentScore: Int, currentLevel: Int): SuccessResult {
        val newScore = currentScore + scoreIncrement
        val progressionUpdate = progression.recordSuccess(currentLevel)
        val nextLevel = progressionUpdate.nextLevel

        if (nextLevel != null) {
            sessionTracker.recordProgress(nextLevel)
        }

        val levelAfterUpdate = nextLevel ?: currentLevel
        sessionTracker.recordSuccess(newScore, levelAfterUpdate)

        return SuccessResult(
            newScore = newScore,
            highScore = sessionTracker.highScore,
            level = levelAfterUpdate,
            didPromoteLevel = nextLevel != null,
            challengesCompleted = progressionUpdate.challengesCompleted,
            challengesPerLevel = progressionUpdate.challengesPerLevel,
            timerLength = progressionUpdate.timerLength
        )
    }

    fun onFailure(currentLives: Int, currentLevel: Int): FailureResult {
        val remainingLives = currentLives - 1
        sessionTracker.recordFailure(currentLevel)
        return FailureResult(
            remainingLives = remainingLives,
            isGameOver = remainingLives <= 0
        )
    }

    data class SuccessResult(
        val newScore: Int,
        val highScore: Int,
        val level: Int,
        val didPromoteLevel: Boolean,
        val challengesCompleted: Int,
        val challengesPerLevel: Int,
        val timerLength: Long
    )

    data class FailureResult(
        val remainingLives: Int,
        val isGameOver: Boolean
    )
}
