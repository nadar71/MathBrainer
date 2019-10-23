package eu.indiewalkabout.mathbrainer.statistics

import eu.indiewalkabout.mathbrainer.util.AppExecutors
import eu.indiewalkabout.mathbrainer.util.SingletonProvider

object Results {

    /**
     * ---------------------------------------------------------------------------------------------
     * Init game results in db
     * ---------------------------------------------------------------------------------------------
     */
    fun initResultsThread() {
        val executorsInstance = (SingletonProvider.getsContext() as SingletonProvider).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
            repository?.initGameResults()
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update game results in db
     * ---------------------------------------------------------------------------------------------
     */
    fun incrementGameResultsThread(gameResultsName: String) {
        val executorsInstance = (SingletonProvider.getsContext() as SingletonProvider).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
            repository?.incrementGameResult(gameResultsName)
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Increment results for game which need to be incremented by more then 1 unit,
     * that is by a delta
     * ---------------------------------------------------------------------------------------------
     */
    fun incrementGameResultByDeltaThread(gameResultsName: String, delta: Int) {
        val executorsInstance = (SingletonProvider.getsContext() as SingletonProvider).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
            repository?.incrementGameResultByDelta(gameResultsName, delta)
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update highscore for a game result if last score is greater
     * ---------------------------------------------------------------------------------------------
     */
    fun updateGameResultHighscoreThread(gameResultsName: String, lastScore: Int) {
        val executorsInstance = (SingletonProvider.getsContext() as SingletonProvider).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
            repository?.updateGameResultHighscore(gameResultsName, lastScore)
        }
    }


}
