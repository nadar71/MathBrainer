package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.state

import androidx.compose.runtime.Immutable
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderChallenge

@Immutable
data class NumberOrderUiState(
    override val challenge: NumberOrderChallenge? = null,
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = 5,
    override val lives: Int = 3,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false,
    val isMemorizing: Boolean = false,
    val selectedNumbers: List<Int> = emptyList(),
    val showNextButton: Boolean = false,
    val revealedCount: Int = 0,
    val challengeId: Int = 0,
):ChallengeUiState<NumberOrderChallenge>(
    challenge = challenge,
    score = score,
    highScore = highScore,
    level = level,
    challengesCompleted = challengesCompleted,
    challengesPerLevel = challengesPerLevel,
    lives = lives,
    feedback = feedback,
    isGameOver = isGameOver
){
    companion object {
        const val INITIAL_MEMORIZE_DURATION = 2_000L
    }
}