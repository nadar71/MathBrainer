package eu.indiewalkabout.mathbrainer.domain.model.results.OLD

import eu.indiewalkabout.mathbrainer.AppMathBrainer

// TODO : to be deleted
object Results {

    // Init game results in db
    fun initResultsThread() {
        val executorsInstance = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).repository
            repository?.initGameResults()
        }
    }

    // Update game results in db
    fun incrementGameResultsThread(gameResultsName: String) {
        val executorsInstance = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).repository
            repository?.incrementGameResult(gameResultsName)
        }
    }

    // Increment results for game which need to be incremented by more then 1 unit, that is by a delta
    fun incrementGameResultByDeltaThread(gameResultsName: String, delta: Int) {
        val executorsInstance = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).repository
            repository?.incrementGameResultByDelta(gameResultsName, delta)
        }
    }

    // Update highscore for a game result if last score is greater
    fun updateGameResultHighscoreThread(gameResultsName: String, lastScore: Int) {
        val executorsInstance = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).appExecutorsInstance
        executorsInstance!!.diskIO().execute {
            val repository = (AppMathBrainer.Companion.getsContext() as AppMathBrainer).repository
            repository?.updateGameResultHighscore(gameResultsName, lastScore)
        }
    }
}