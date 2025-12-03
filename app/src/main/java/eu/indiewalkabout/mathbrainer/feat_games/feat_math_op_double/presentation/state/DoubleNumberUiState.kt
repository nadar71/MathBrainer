package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.state

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model.DoubleNumberChallenge

// TODO : migrate this module to a clean architecture if needed in future iterations.
data class DoubleNumberUiState(
    val challenge: DoubleNumberChallenge? = null,
    val inputValue: String = "",
    val score: Int = 0,
    val highScore: Int? = null,
    val level: Int = 1,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = 10,
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
        const val INITIAL_TIMER_LENGTH: Long = 30_000L
    }
}