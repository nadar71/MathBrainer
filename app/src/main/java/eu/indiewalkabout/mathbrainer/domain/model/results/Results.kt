package eu.indiewalkabout.mathbrainer.domain.model.results

import eu.indiewalkabout.mathbrainer.AppMathBrainer

object Results {

    /**
     * ---------------------------------------------------------------------------------------------
     * Init game results in db
     * ---------------------------------------------------------------------------------------------
     */
    fun initResultsThread() {
        val executorsInstance = (AppMathBrainer.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.getsContext() as AppMathBrainer).repository
            repository?.initGameResults()
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update game results in db
     * ---------------------------------------------------------------------------------------------
     */
    fun incrementGameResultsThread(gameResultsName: String) {
        val executorsInstance = (AppMathBrainer.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.getsContext() as AppMathBrainer).repository
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
        val executorsInstance = (AppMathBrainer.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.getsContext() as AppMathBrainer).repository
            repository?.incrementGameResultByDelta(gameResultsName, delta)
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update highscore for a game result if last score is greater
     * ---------------------------------------------------------------------------------------------
     */
    fun updateGameResultHighscoreThread(gameResultsName: String, lastScore: Int) {
        val executorsInstance = (AppMathBrainer.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.getsContext() as AppMathBrainer).repository
            repository?.updateGameResultHighscore(gameResultsName, lastScore)
        }
    }


}
