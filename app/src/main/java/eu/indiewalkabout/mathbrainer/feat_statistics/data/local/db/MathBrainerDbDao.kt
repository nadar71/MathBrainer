package eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStatistics


// test
@Dao
interface MathBrainerDbDao {

    // ----------------------------------------------------------------------------------------------
    //  QUERY
    // ----------------------------------------------------------------------------------------------

    @Query("SELECT * FROM GameScores ")
    suspend fun loadGameScores(): GameScores

    @Query("SELECT * FROM GameStatistics")
    suspend fun loadGameStatistics(): GameStatistics


    // ----------------------------------------------------------------------------------------------
    //  INSERT
    // ----------------------------------------------------------------------------------------------
    // TODO : to be removed

    @Insert
    fun insertGameScores(gameScores: GameScores)

    @Insert
    fun insertGameStatistics(gameStatistics: GameStatistics)

    // ----------------------------------------------------------------------------------------------
    //  UPDATE
    // ----------------------------------------------------------------------------------------------

    @Delete
    fun deleteGameScores(gameScores: GameScores)
    @Delete
    fun deleteGameStatistics(gameStatistics: GameStatistics)

    // ----------------------------------------------------------------------------------------------
    //  DROPS
    // ----------------------------------------------------------------------------------------------

    @Query("DELETE FROM GameScores")
    fun dropTableGameScores()

    @Query("DELETE FROM GameStatistics")
    fun dropTableGameStatistics()
}
