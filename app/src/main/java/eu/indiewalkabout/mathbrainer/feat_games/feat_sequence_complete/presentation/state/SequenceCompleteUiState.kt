package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.state

data class SequenceCompleteUiState(
    val visibleSequence: List<Int?> = emptyList(),
    val inputValue: String = "",
    val score: Int = 0,
    val highScore: Int? = null,
    val level: Int = 1,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = INITIAL_CHALLENGES_PER_LEVEL,
    val lives: Int = 3,
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false,
    val ruleDescription: String? = null,
    val revealedAnswer: Int? = null,
    val isReadyForNext: Boolean = false,
    val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    val totalTime: Long = INITIAL_TIMER_LENGTH
) {
    val timerProgress: Float
        get() = if (totalTime == 0L) 0f else (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)

    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_TIMER_LENGTH: Long = 20_000L
        const val INITIAL_CHALLENGES_PER_LEVEL: Int = 5
    }
}
