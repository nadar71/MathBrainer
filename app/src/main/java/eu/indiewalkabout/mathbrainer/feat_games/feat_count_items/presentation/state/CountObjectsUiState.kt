package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.state

import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model.CountObjectsChallenge

data class CountObjectsUiState(
    val level: Int = 1,
    val score: Int = 0,
    val lives: Int = 3,
    val highScore: Int? = null,
    val challenge: CountObjectsChallenge? = null,
    val isShowingItems: Boolean = false,
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = 1,
    val memorizeDurationMs: Long = INITIAL_MEMORIZE_DURATION,
    val showNextButton: Boolean = false,
    val challengeId: Int = 0
) {
    val timerProgress: Float
        get() = if (isShowingItems) 1f else 0f

    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_MEMORIZE_DURATION = 2_000L
    }
}