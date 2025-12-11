package eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import kotlinx.coroutines.flow.Flow

interface MathBrainerRepository {
    // --- QUERY ---
    suspend fun observeGameScores(): Flow<GameScores?>
    suspend fun observeMathWriteGameStats(): Flow<List<MathWriteGameStats>>
    suspend fun getMathWriteGameStats(operationId: String): MathWriteGameStats?
    suspend fun loadGameStatistics(): GameStatistics

    // --- INSERT ---
    suspend fun insertGameScores(gameScores: GameScores)
    suspend fun insertGameStatistics(gameStatistics: GameStatistics)
    suspend fun insertMathWriteGameStats(mathWriteGameStats: MathWriteGameStats)

    // --- DROPS ---
    suspend fun dropTableGameScores()
    suspend fun dropTableGameStatistics()
    suspend fun dropTableMathWriteGameStats()
}