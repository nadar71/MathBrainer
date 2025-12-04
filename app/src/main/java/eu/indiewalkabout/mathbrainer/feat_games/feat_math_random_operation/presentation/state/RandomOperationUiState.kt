import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationChallenge

data class RandomOperationUiState(
    val challenge: RandomOperationChallenge? = null,
    val score: Int = 0,
    val highScore: Int? = null,
    val level: Int = 1,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = 12,
    val lives: Int = 3,
    val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    val totalTime: Long = INITIAL_TIMER_LENGTH,
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false
) {
    val timerProgress: Float
        get() = if (totalTime == 0L) 0f else (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)

    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_TIMER_LENGTH: Long = 20_000L
    }
}