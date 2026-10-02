package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.state

import androidx.compose.runtime.Immutable
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaChallenge

@Immutable
data class EnigmaUiState(
    override val challenge: EnigmaChallenge? = null,
    override val inputValue: String = "",
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = 5,
    override val lives: Int = 3,
    override val timeRemaining: Long = INITIAL_TIMER_LENGTH,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false
) : ChallengeUiState<EnigmaChallenge>(
    challenge = challenge,
    inputValue = inputValue,
    score = score,
    highScore = highScore,
    level = level,
    challengesCompleted = challengesCompleted,
    challengesPerLevel = challengesPerLevel,
    lives = lives,
    timeRemaining = timeRemaining,
    feedback = feedback,
    isGameOver = isGameOver
)