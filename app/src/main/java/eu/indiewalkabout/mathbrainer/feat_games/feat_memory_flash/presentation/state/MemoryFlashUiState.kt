package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.state

data class MemoryFlashUiState(
    val score: Int = 0,
    val highScore: Int? = null,
    val level: Int = 1,
    val lives: Int = 3,
    val visibleSequence: String = "",
    val isSequenceVisible: Boolean = true,
    val inputValue: String = "",
    val feedback: Feedback? = null,
    val isGameOver: Boolean = false,
    val challengesCompleted: Int = 0,
    val challengesPerLevel: Int = INITIAL_CHALLENGES_PER_LEVEL,
    val revealedSequence: String? = null,
    val isReadyForNext: Boolean = false
) {
    enum class Feedback { SUCCESS, FAILURE }

    companion object {
        const val INITIAL_CHALLENGES_PER_LEVEL = 3
        const val MAX_CHALLENGES_PER_LEVEL = 6
    }
}
