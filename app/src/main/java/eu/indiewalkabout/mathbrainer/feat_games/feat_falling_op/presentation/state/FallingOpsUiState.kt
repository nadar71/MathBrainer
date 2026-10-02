package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState

@Immutable
data class FallingOpsUiState(
    override val score: Int = 0,
    override val highScore: Int? = null,
    override val level: Int = 1,
    override val lives: Int = 3,
    override val feedback: Feedback? = null,
    override val isGameOver: Boolean = false,
    val targetPerLevel: Int = 10,
    val explodedThisLevel: Int = 0,
    val operations: List<FallingOperationItem> = emptyList(),
    val input: String = "",
) : ChallengeUiState<Challenge>(
    score = score,
    highScore = highScore,
    level = level,
    lives = lives,
    feedback = feedback,
    isGameOver = isGameOver
) {
    data class FallingItem(
        val id: Int,
        val value: Int,
        val position: Offset,
        val speed: Float,
        val isCaught: Boolean = false
    )
}
