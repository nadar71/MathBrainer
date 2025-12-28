package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores.Companion.emptyScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import javax.inject.Inject
import kotlin.math.max
import kotlinx.coroutines.flow.firstOrNull

class UpdateMemoryFlashScoreUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(sessionScore: Int) {
        val currentScores = repository.observeGameScores().firstOrNull() ?: emptyScores
        val updatedScores = currentScores.copy(
            memory_flash_game_score = max(currentScores.memory_flash_game_score, sessionScore),
            id = 0,
            global_score = currentScores.global_score + sessionScore
        )
        repository.insertGameScores(updatedScores)
    }
}
