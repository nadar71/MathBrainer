package eu.indiewalkabout.mathbrainer.presentation.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import eu.indiewalkabout.mathbrainer.AppMathBrainer
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.data.repository.MathBrainerRepository
import eu.indiewalkabout.mathbrainer.domain.model.results.GameResult

// ViewModel Class for retrieving games results
class HighScoresViewModel : ViewModel() {
    val gameResultsList: LiveData<List<GameResult>>
    private val repository: MathBrainerRepository

    init {
        Log.d(TAG, "Retrieving game results list from repository")
        // get repository instance
        repository = (AppMathBrainer.getsContext() as AppMathBrainer).repository!!
        gameResultsList = repository.allGamesResults
    }
}
