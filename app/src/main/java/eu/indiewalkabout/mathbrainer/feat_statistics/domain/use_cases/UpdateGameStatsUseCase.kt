package eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases

import android.util.Log
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import javax.inject.Inject
import kotlin.math.max

class UpdateGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(previousStats: GameStats?, updatedStats: GameStats) {
        if (updatedStats.gameId.isBlank()) return

        val currentStats = previousStats ?:
                repository.getGameStats(updatedStats.gameId) ?:
                GameStats(gameId = updatedStats.gameId)

        val mergedStats = updatedStats.copy(
            highScore = max(currentStats.highScore, updatedStats.highScore),
            lastLevel = max(currentStats.lastLevel, updatedStats.lastLevel)
        )
        Log.d("UpdateGameStatsUseCase", "Merged stats: $mergedStats")
        repository.insertGameStats(mergedStats)
    }
}
