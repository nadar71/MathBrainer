package eu.indiewalkabout.mathbrainer.data.repository


import androidx.lifecycle.LiveData
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDatabase
import eu.indiewalkabout.mathbrainer.domain.model.results.GameResult

import java.util.ArrayList

/**
 * -------------------------------------------------------------------------------------------------
 * App repository
 * -------------------------------------------------------------------------------------------------
 */
class MathBrainerRepository private constructor(private val mathBrainerDB: MathBrainerDatabase) {

    val game_results_list = ArrayList<String>()


    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve all games results
    val allGamesResults: LiveData<List<GameResult>>
        get() = mathBrainerDB.mathBrainerDbDao().loadAllGamesResults()


    init {

        game_results_list.add("operations_executed")
        game_results_list.add("operations_ok")
        game_results_list.add("operations_ko")
        game_results_list.add("sums")
        game_results_list.add("differences")
        game_results_list.add("multiplications")
        game_results_list.add("divisions")
        game_results_list.add("doublings")
        game_results_list.add("level_upgrades")
        game_results_list.add("lives_missed")
        game_results_list.add("objects_counted")
        game_results_list.add("numbers_in_order")
        game_results_list.add("games_played")
        game_results_list.add("games_lose")

        game_results_list.add("global_score")
        game_results_list.add("doublenumber_game_score")

        game_results_list.add("sum_choose_result_game_score")
        game_results_list.add("diff_choose_result_game_score")
        game_results_list.add("mult_choose_result_game_score")
        game_results_list.add("div_choose_result_game_score")
        game_results_list.add("mix_choose_result_game_score")


        game_results_list.add("sum_write_result_game_score")
        game_results_list.add("diff_write_result_game_score")
        game_results_list.add("mult_write_result_game_score")
        game_results_list.add("div_write_result_game_score")
        game_results_list.add("mix_write_result_game_score")


        game_results_list.add("random_op_game_score")
        game_results_list.add("count_objects_game_score")
        game_results_list.add("number_order_game_score")

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check if game results are initialized (present with  0, so ok), otherwise init them.
     * ---------------------------------------------------------------------------------------------
     */
    fun initGameResults() {
        for (gameResultName in game_results_list) {
            if (getGameResult(gameResultName) < 0) {  // result not present, init
                val gameResultNew = GameResult(gameResultName, 0)
                mathBrainerDB.mathBrainerDbDao().insertGameResult(gameResultNew)
            }
        }
    }

    // retrieve a specific game result
    fun getGameResult(gameResult: String): Int {
        return if (mathBrainerDB.mathBrainerDbDao().isGameResultExists(gameResult) != null) {
            mathBrainerDB.mathBrainerDbDao().getGameResult(gameResult)
        } else -1
    // game result not present and then not initialized
    }


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    fun insertGameResult(gameResult: GameResult) {
        mathBrainerDB.mathBrainerDbDao().insertGameResult(gameResult)
    }


    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------
    fun incrementGameResult(gameResultName: String) {
        val previousResult = mathBrainerDB.mathBrainerDbDao().getGameResult(gameResultName)
        mathBrainerDB.mathBrainerDbDao().updateGameResult(gameResultName, previousResult + 1)
    }


    fun incrementGameResultByDelta(gameResultName: String, delta: Int) {
        val previousResult = mathBrainerDB.mathBrainerDbDao().getGameResult(gameResultName)
        mathBrainerDB.mathBrainerDbDao().updateGameResult(gameResultName, previousResult + delta)
    }


    fun updateGameResultHighscore(gameResultName: String, lastScore: Int) {
        val previousResult = mathBrainerDB.mathBrainerDbDao().getGameResult(gameResultName)
        if (previousResult < lastScore) {
            mathBrainerDB.mathBrainerDbDao().updateGameResult(gameResultName, lastScore)
        } else {
            return
        }

    }


    // delete single record
    fun deleteGameResult(gameResult: GameResult) {
        mathBrainerDB.mathBrainerDbDao().deleteGameResult(gameResult)
    }

    companion object {

        private val TAG = MathBrainerRepository::class.java.simpleName

        private var sInstance: MathBrainerRepository? = null


        /**
         * ---------------------------------------------------------------------------------------------
         * Get singleton instance
         * @param database
         * @return
         * ---------------------------------------------------------------------------------------------
         */
        fun getInstance(database: MathBrainerDatabase): MathBrainerRepository? {
            if (sInstance == null) {
                synchronized(MathBrainerRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = MathBrainerRepository(database)
                    }
                }
            }
            return sInstance
        }
    }


}
