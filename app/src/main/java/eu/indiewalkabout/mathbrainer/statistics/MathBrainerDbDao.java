package eu.indiewalkabout.mathbrainer.statistics;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface MathBrainerDbDao {

    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------

    // check if game result is in db;is return string null it isn't
    @Query("SELECT * FROM GAMERESULTS_LIST WHERE result_name = :gameResultName")
    GameResult isGameResultExists(String gameResultName);

    // get game result value
    @Query("SELECT result_value FROM GAMERESULTS_LIST WHERE result_name = :gameResultName")
    int getGameResult(String gameResultName);


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    @Insert
    void insertGameResult(GameResult gameResult);



    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateGameResult(GameResult gameResult);

    @Query("UPDATE GAMERESULTS_LIST SET result_value = :value WHERE result_name = :gameResultName")
    void updateGameResult(String gameResultName, int value);

    // delete single record
    @Delete
    void deleteGameResult(GameResult gameResult);


    //----------------------------------------------------------------------------------------------
    //  DROP TABLE
    //----------------------------------------------------------------------------------------------
    // drop table
    @Query("DELETE FROM GAMERESULTS_LIST")
    public void dropTable();

}
