package eu.indiewalkabout.mathbrainer.feat_statistics.data.repository

import eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDbDao
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MathBrainerRepositoryImpl @Inject constructor(
    private val mathBrainerDbDao: MathBrainerDbDao
): MathBrainerRepository {

    // ---------------------------------------- QUERY ----------------------------------------------

    override fun observeGameScores(): Flow<GameScores?> {
        return mathBrainerDbDao.observeGameScores()
    }

    override fun observeGameStats(): Flow<List<GameStats>> {
        return mathBrainerDbDao.observeGameStats()
    }

    override suspend fun getGameStats(gameId: String): GameStats? {
        return mathBrainerDbDao.getGameStats(gameId)
    }

    override suspend fun loadGameStatistics(): GameStatistics {
        return mathBrainerDbDao.loadGameStatistics()
    }

    override suspend fun insertGameScores(gameScores: GameScores) {
        mathBrainerDbDao.insertGameScores(gameScores)
    }

    override suspend fun insertGameStatistics(gameStatistics: GameStatistics) {
        mathBrainerDbDao.insertGameStatistics(gameStatistics)
    }

    override suspend fun insertGameStats(gameStats: GameStats) {
        mathBrainerDbDao.insertGameStats(gameStats)
    }

    //------------------------------------------- DROPS --------------------------------------------
    override suspend fun dropTableGameScores() {
        mathBrainerDbDao.dropTableGameScores()
    }

    override suspend fun dropTableGameStatistics() {
        mathBrainerDbDao.dropTableGameStatistics()
    }

    override suspend fun dropTableGameStats() {
        mathBrainerDbDao.dropTableGameStats()
    }
}
