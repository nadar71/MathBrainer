package eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics
import kotlinx.coroutines.flow.Flow


@Dao
interface MathBrainerDbDao {

    // --- QUERY ---
    @Query("SELECT * FROM GameScores ORDER BY id DESC LIMIT 1")
    fun observeGameScores(): Flow<GameScores?>

    @Query("SELECT * FROM MathWriteGameStats")
    fun observeMathWriteGameStats(): Flow<List<MathWriteGameStats>>

    @Query("SELECT * FROM MathWriteGameStats WHERE operationId = :operationId LIMIT 1")
    suspend fun getMathWriteGameStats(operationId: String): MathWriteGameStats?

    @Query("SELECT * FROM GameStatistics")
    suspend fun loadGameStatistics(): GameStatistics

    // --- INSERT ---
    @Insert
    suspend fun insertGameScores(gameScores: GameScores)

    @Insert
    suspend fun insertGameStatistics(gameStatistics: GameStatistics)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMathWriteGameStats(mathWriteGameStats: MathWriteGameStats)

    // --- UPDATE ---
    @Delete
    suspend fun deleteGameScores(gameScores: GameScores)
    @Delete
    suspend fun deleteGameStatistics(gameStatistics: GameStatistics)

    // --- DROPS ---
    @Query("DELETE FROM GameScores")
    suspend fun dropTableGameScores()

    @Query("DELETE FROM GameStatistics")
    suspend fun dropTableGameStatistics()

    @Query("DELETE FROM MathWriteGameStats")
    suspend fun dropTableMathWriteGameStats()
}
