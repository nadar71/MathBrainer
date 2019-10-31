package eu.indiewalkabout.mathbrainer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import android.util.Log

import eu.indiewalkabout.mathbrainer.statistics.GameResult
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerRepository
import eu.indiewalkabout.mathbrainer.util.SingletonProvider


/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving games results
 * -------------------------------------------------------------------------------------------------
 */
class HighScoresViewModel : ViewModel() {

    val gameResultsList: LiveData<List<GameResult>>

    private val repository: MathBrainerRepository

    init {
        Log.d(TAG, "Retrieving game results list from repository")

        // get repository instance
        repository = (SingletonProvider.getsContext() as SingletonProvider).repository!!
        gameResultsList = repository.allGamesResults
    }

    companion object {

        private val TAG = HighScoresViewModel::class.java.simpleName
    }


}
