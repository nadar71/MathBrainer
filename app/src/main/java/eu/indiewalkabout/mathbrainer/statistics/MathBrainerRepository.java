package eu.indiewalkabout.mathbrainer.statistics;


import android.arch.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

/**
 * -------------------------------------------------------------------------------------------------
 * App repository
 * -------------------------------------------------------------------------------------------------
 */
public class MathBrainerRepository {

    private static final String TAG = MathBrainerRepository.class.getSimpleName();

    private static MathBrainerRepository sInstance;

    private final MathBrainerDatabase mathBrainerDB;

    private final List<String> game_results_list = new ArrayList<String>();




    private MathBrainerRepository(MathBrainerDatabase db){
        this.mathBrainerDB = db;

        game_results_list.add("operations_executed");
        game_results_list.add("operations_ok");
        game_results_list.add("operations_ko");
        game_results_list.add("sums");
        game_results_list.add("differences");
        game_results_list.add("multiplications");
        game_results_list.add("divisions");
        game_results_list.add("doublings");
        game_results_list.add("level_upgrades");
        game_results_list.add("lifes_missed");
        game_results_list.add("objects_counted");
        game_results_list.add("numbers_in_order");
        game_results_list.add("games_played");
        game_results_list.add("games_lose");

        game_results_list.add("global_score");
        game_results_list.add("doublenumber_game_score");

        game_results_list.add("sum_choose_result_game_score");
        game_results_list.add("diff_choose_result_game_score");
        game_results_list.add("mult_choose_result_game_score");
        game_results_list.add("div_choose_result_game_score");
        game_results_list.add("mix_choose_result_game_score");


        game_results_list.add("sum_write_result_game_score");
        game_results_list.add("diff_write_result_game_score");
        game_results_list.add("mult_write_result_game_score");
        game_results_list.add("div_write_result_game_score");
        game_results_list.add("mix_write_result_game_score");


        game_results_list.add("random_op_game_score");
        game_results_list.add("count_objects_game_score");
        game_results_list.add("number_order_game_score");

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Get singleton instance
     * @param database
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static MathBrainerRepository getInstance(final MathBrainerDatabase database) {
        if (sInstance == null) {
            synchronized (MathBrainerRepository.class) {
                if (sInstance == null) {
                    sInstance = new MathBrainerRepository(database);
                }
            }
        }
        return sInstance;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *   Check if game results are initialized (present with  0, so ok), otherwise init them.
     * ---------------------------------------------------------------------------------------------
     */
    public void initGameResults(){
        for(String gameResultName : game_results_list){
            if (getGameResult(gameResultName) < 0 ) {  // result not present, init
                GameResult gameResultNew = new GameResult(gameResultName, 0);
                mathBrainerDB.MathBrainerDbDao().insertGameResult(gameResultNew);
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve all games results
    public LiveData<List<GameResult>> getAllGamesResults(){
        return mathBrainerDB.MathBrainerDbDao().loadAllGamesResults();
    }

    // retrieve a specific game result
    public int getGameResult(String gameResult){
        if ( mathBrainerDB.MathBrainerDbDao().isGameResultExists(gameResult) != null ) {
            return mathBrainerDB.MathBrainerDbDao().getGameResult(gameResult);
        }
        return -1; // game result not present and then not initialized
    }


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    public void insertGameResult(GameResult gameResult){
        mathBrainerDB.MathBrainerDbDao().insertGameResult(gameResult);
    }



    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------
    public void incrementGameResult(String gameResultName){
        int previousResult = mathBrainerDB.MathBrainerDbDao().getGameResult(gameResultName);
        mathBrainerDB.MathBrainerDbDao().updateGameResult(gameResultName,previousResult+1 );
    }



    public void incrementGameResultByDelta(String gameResultName, int delta){
        int previousResult = mathBrainerDB.MathBrainerDbDao().getGameResult(gameResultName);
        mathBrainerDB.MathBrainerDbDao().updateGameResult(gameResultName,previousResult+delta );
    }


    public void updateGameResultHighscore(String gameResultName, int lastScore){
        int previousResult = mathBrainerDB.MathBrainerDbDao().getGameResult(gameResultName);
        if (previousResult < lastScore) {
            mathBrainerDB.MathBrainerDbDao().updateGameResult(gameResultName, lastScore);
        }else{
            return;
        }

    }



    // delete single record
    public void deleteGameResult(GameResult gameResult){
        mathBrainerDB.MathBrainerDbDao().deleteGameResult(gameResult);
    }


}
