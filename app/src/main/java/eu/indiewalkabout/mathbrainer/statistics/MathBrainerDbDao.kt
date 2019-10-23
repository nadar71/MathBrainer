package eu.indiewalkabout.mathbrainer.statistics

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface MathBrainerDbDao {

    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------

    // retrieve ALL KIND OF FOOD  without regarding exipring date
    @Query("SELECT * FROM GAMERESULTS_LIST ")
    fun loadAllGamesResults(): LiveData<List<GameResult>>

    // check if game result is in db;is return string null it isn't
    @Query("SELECT * FROM GAMERESULTS_LIST WHERE result_name = :gameResultName")
    fun isGameResultExists(gameResultName: String): GameResult

    // get game result value
    @Query("SELECT result_value FROM GAMERESULTS_LIST WHERE result_name = :gameResultName")
    fun getGameResult(gameResultName: String): Int


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    @Insert
    fun insertGameResult(gameResult: GameResult)


    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateGameResult(gameResult: GameResult)

    @Query("UPDATE GAMERESULTS_LIST SET result_value = :value WHERE result_name = :gameResultName")
    fun updateGameResult(gameResultName: String, value: Int)

    // delete single record
    @Delete
    fun deleteGameResult(gameResult: GameResult)


    //----------------------------------------------------------------------------------------------
    //  DROP TABLE
    //----------------------------------------------------------------------------------------------
    // drop table
    @Query("DELETE FROM GAMERESULTS_LIST")
    fun dropTable()

}
