package eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import javax.inject.Inject
import kotlin.math.max

class UpdateGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(gameId: String, sessionScore: Int, isWin: Boolean, lastLevel: Int) {
        if (gameId.isBlank()) return

        // TODO: must be passed as parameters
        val currentStats = repository.getGameStats(gameId) ?: GameStats(gameId = gameId)

        val shouldUpdateHighScore = currentStats.highScore == 0 || sessionScore >= currentStats.highScore

        val updatedStats = currentStats.copy(
            highScore = if (shouldUpdateHighScore) sessionScore else currentStats.highScore,
            challengesPlayed = currentStats.challengesPlayed + 1,
            challengesWon = currentStats.challengesWon + if (isWin) 1 else 0,
            challengesLost = currentStats.challengesLost + if (isWin) 0 else 1,
            lastLevel = max(currentStats.lastLevel, lastLevel)
        )

        repository.insertGameStats(updatedStats)
    }
}
