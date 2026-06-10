package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.state

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.state.SequenceCompleteUiState.Companion.INITIAL_CHALLENGES_PER_LEVEL

data class MemoryFlashUiState(
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val lives: Int = 3,
    override val inputValue: String = "",
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false,
    override val challengesCompleted: Int = 0,
    override val challengesPerLevel: Int = INITIAL_CHALLENGES_PER_LEVEL,
    val visibleSequence: String = "",
    val isSequenceVisible: Boolean = true,
    val revealedSequence: String? = null,
    val isReadyForNext: Boolean = false
): ChallengeUiState<Challenge>(
    inputValue = inputValue,
    score = score,
    highScore = highScore,
    level = level,
    challengesCompleted = challengesCompleted,
    challengesPerLevel = challengesPerLevel,
    lives = lives,
    feedback = feedback,
    isGameOver = isGameOver
)/*{
    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_CHALLENGES_PER_LEVEL = 3
        const val MAX_CHALLENGES_PER_LEVEL = 6
    }
}*/
