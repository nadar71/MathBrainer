package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import javax.inject.Inject

class UpdateMathWriteGameStatsUseCase @Inject constructor(
    private val repository: MathBrainerRepository
) {
    suspend operator fun invoke(operation: String, sessionScore: Int, isWin: Boolean) {
        if (operation.isBlank()) return

        val currentStats = repository.getMathWriteGameStats(operation)
            ?: MathWriteGameStats(operationId = operation)

        val shouldUpdateHighScore = currentStats.highScore == 0 || sessionScore >= currentStats.highScore

        val updatedStats = currentStats.copy(
            highScore = if (shouldUpdateHighScore) sessionScore else currentStats.highScore,
            gamesPlayed = currentStats.gamesPlayed + 1,
            gamesWon = currentStats.gamesWon + if (isWin) 1 else 0,
            gamesLost = currentStats.gamesLost + if (isWin) 0 else 1
        )

        repository.insertMathWriteGameStats(updatedStats)
    }
}
