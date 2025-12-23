package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.state

import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaChallenge

data class EnigmaUiState(
    val challenge: EnigmaChallenge? = null,
    val inputValue: String = "",
    val score: Int = 0,
    val highScore: Int? = null,
    val level: Int = 1,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = 3,
    val lives: Int = 3,
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false
) {
    enum class Feedback { SUCCESS, FAILURE }
}
