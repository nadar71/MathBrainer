package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.state

import androidx.compose.runtime.Immutable
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteChallenge

@Immutable
data class MathWriteUiState(
    override val challenge: MathWriteChallenge? = null,
    override val inputValue: String = "",
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = 10,
    override val lives: Int = 3,
    override val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    override val totalTime: Long = INITIAL_TIMER_LENGTH,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false
) : ChallengeUiState<MathWriteChallenge>(
    challenge = challenge,
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
    isGameOver = isGameOver
)