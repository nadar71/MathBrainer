package eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import javax.inject.Inject

class GetGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(gameId: String): GameStats? {
        if (gameId.isBlank()) return null
        return repository.getGameStats(gameId) ?: GameStats(gameId = gameId)
    }
}
