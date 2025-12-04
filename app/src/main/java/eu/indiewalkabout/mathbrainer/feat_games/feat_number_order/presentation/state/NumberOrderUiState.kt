package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.state

import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderChallenge

data class NumberOrderUiState(
    val level: Int = 1,
    val lives: Int = 3,
    val score: Int = 0,
    val highScore: Int? = null,
    val challenge: NumberOrderChallenge? = null,
    val isMemorizing: Boolean = false,
    val feedback: Feedback? = null,
    val showNextButton: Boolean = false,
    val revealedCount: Int = 0,
    val challengeId: Int = 0,
    val isGameOver: Boolean = false
) {
    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_MEMORIZE_DURATION = 2_000L
    }
}