package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.state

import androidx.compose.runtime.Immutable
import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState

@Immutable
data class SequenceCompleteUiState(
    override val inputValue: String = "",
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = INITIAL_CHALLENGES_PER_LEVEL,
    override val lives: Int = 3,
    override val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    override val totalTime: Long = INITIAL_TIMER_LENGTH,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false,
    val visibleSequence: List<Int?> = emptyList(),
    val ruleDescription: String? = null,
    val revealedAnswer: Int? = null,
    val isReadyForNext: Boolean = false,
):ChallengeUiState<Challenge>(
    challenge = null,
    inputValue = inputValue,
    score = score,
    highScore = highScore,
    level = level,
    challengesCompleted = challengesCompleted,
    challengesPerLevel = challengesPerLevel,
    lives = lives,
    timeRemaining = timeRemaining,
    totalTime = totalTime,
    feedback = feedback,
    isGameOver = isGameOver,
) {
    companion object {
        const val INITIAL_CHALLENGES_PER_LEVEL: Int = 5
    }
}
