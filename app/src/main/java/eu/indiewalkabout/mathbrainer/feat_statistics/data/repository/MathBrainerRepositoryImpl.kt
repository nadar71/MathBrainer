package eu.indiewalkabout.mathbrainer.feat_statistics.data.repository

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDbDao
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MathBrainerRepositoryImpl @Inject constructor(
    private val mathBrainerDbDao: MathBrainerDbDao
): MathBrainerRepository {

    // ---------------------------------------- QUERY ----------------------------------------------

    override suspend fun observeGameScores(): Flow<GameScores?> {
        return mathBrainerDbDao.observeGameScores()
    }

    override suspend fun observeMathWriteGameStats(): Flow<List<MathWriteGameStats>> {
        return mathBrainerDbDao.observeMathWriteGameStats()
    }

    override suspend fun getMathWriteGameStats(operationId: String): MathWriteGameStats? {
        return mathBrainerDbDao.getMathWriteGameStats(operationId)
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

    override suspend fun insertMathWriteGameStats(mathWriteGameStats: MathWriteGameStats) {
        mathBrainerDbDao.insertMathWriteGameStats(mathWriteGameStats)
    }

    //------------------------------------------- DROPS --------------------------------------------
    override suspend fun dropTableGameScores() {
        mathBrainerDbDao.dropTableGameScores()
    }

    override suspend fun dropTableGameStatistics() {
        mathBrainerDbDao.dropTableGameStatistics()
    }

    override suspend fun dropTableMathWriteGameStats() {
        mathBrainerDbDao.dropTableMathWriteGameStats()
    }
}