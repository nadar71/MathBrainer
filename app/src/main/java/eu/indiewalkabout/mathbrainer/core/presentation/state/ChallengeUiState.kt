package eu.indiewalkabout.mathbrainer.core.presentation.state

import androidx.compose.runtime.Immutable

// Base UI state for all game screens.
@Immutable
open class ChallengeUiState<out T : Challenge?>(
    open val challenge: T? = null,
    open val inputValue: String = "",
    open val score: Int = 0,
    open val highScore: Int? = null,
    open val level: Int = 1,
    open val challengesCompleted: Int = 0,
    open val challengesPerLevel: Int = 10,
    open val lives: Int = 3,
    open val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    open val totalTime: Long = INITIAL_TIMER_LENGTH,
    open val feedback: Feedback? = null,
    open val isGameOver: Boolean = false
) {
    val timerProgress: Float
        get() = if (totalTime == 0L) 0f else (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)

    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_TIMER_LENGTH: Long = 20_000L
    }
}
