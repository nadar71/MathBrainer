package eu.indiewalkabout.mathbrainer.statistics

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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
