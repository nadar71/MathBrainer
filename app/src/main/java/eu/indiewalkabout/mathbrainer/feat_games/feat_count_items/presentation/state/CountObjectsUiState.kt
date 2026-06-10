package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.state

import androidx.compose.runtime.Immutable
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model.CountObjectsChallenge

@Immutable
data class CountObjectsUiState(
    override val challenge: CountObjectsChallenge? = null,
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val lives: Int = 3,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = 1,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false,
    val selectedCount: Int = 0,
    val isAnswerShown: Boolean = false,
    val isShowingItems: Boolean = false,
    val memorizeDurationMs: Long = INITIAL_MEMORIZE_DURATION,
    val showNextButton: Boolean = false,
    val challengeId: Int = 0
) : ChallengeUiState<CountObjectsChallenge>(
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