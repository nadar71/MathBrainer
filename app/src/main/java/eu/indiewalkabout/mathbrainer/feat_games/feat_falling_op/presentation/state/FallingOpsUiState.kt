package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.state

data class FallingOpsUiState(
    val level: Int = 1,
    val score: Int = 0,
    val lives: Int = 3,
    val highScore: Int? = null,
    val targetPerLevel: Int = 10,
    val explodedThisLevel: Int = 0,
    val operations: List<FallingOperationItem> = emptyList(),
    val input: String = "",
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false
) {
    enum class Feedback {
        SUCCESS,
        FAILURE
    }
}
