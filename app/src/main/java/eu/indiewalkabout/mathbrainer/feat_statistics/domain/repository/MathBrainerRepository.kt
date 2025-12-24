package eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import kotlinx.coroutines.flow.Flow

interface MathBrainerRepository {
    // --- QUERY ---
    suspend fun observeGameScores(): Flow<GameScores?>
    suspend fun observeGameStats(): Flow<List<GameStats>>
    suspend fun getGameStats(gameId: String): GameStats?
    suspend fun loadGameStatistics(): GameStatistics

    // --- INSERT ---
    suspend fun insertGameScores(gameScores: GameScores)
    suspend fun insertGameStatistics(gameStatistics: GameStatistics)
    suspend fun insertGameStats(gameStats: GameStats)

    // --- DROPS ---
    suspend fun dropTableGameScores()
    suspend fun dropTableGameStatistics()
    suspend fun dropTableGameStats()
}
