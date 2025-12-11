package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_home.data.local.gamesDefinitionsList
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetGameScoresUseCase
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetMathWriteGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_home.presentation.state.HomeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGameScoresUseCase: GetGameScoresUseCase,
    private val getMathWriteGameStatsUseCase: GetMathWriteGameStatsUseCase
) : ViewModel() {

    private val TAG = "HomeViewModel"
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var scoresJob: Job? = null

    init {
        Log.d(TAG, "Initializing HomeViewModel")
        refresh()
    }

    fun refresh() {
        Log.d(TAG, "Refreshing game scores")
        scoresJob?.cancel()
        scoresJob = viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try{
                combine(
                    getGameScoresUseCase(),
                    getMathWriteGameStatsUseCase()
                ) { scores, stats ->
                    Pair(scores, stats)
                }.collect { (scores, stats) ->
                    Log.d(TAG, "Received game scores: $scores")
                    val games = gamesDefinitionsList.map { definition ->
                        val highScore = getHighScore(definition, scores)
                        val mathWriteStats = getMathWriteStats(definition, stats)
                        Log.d(TAG, "Game: ${definition.id}, High Score: $highScore")
                        GameUiModel(
                            definition = definition,
                            highScore = highScore,
                            mathWriteStats = mathWriteStats
                        )
                    }
                    _uiState.value = HomeUiState(isLoading = false, games = games)
                }
            } catch (e: Exception) {
            Log.e(TAG, "Error loading game scores", e)
            _uiState.value = HomeUiState(
                isLoading = false,
                error = e.message ?: "Unknown error"
            )
        }
        }
    }

    private fun getHighScore(definition: GameDefinition, scores: GameScores): Int? {
        return try {
            val gameType = GameTypes.fromId(definition.id)
            Log.d(TAG, "Getting high score for game: ${definition.id}")
            Log.d(TAG, "GameType found: ${gameType?.name ?: "null"}")

            if (gameType == null) {
                Log.e(TAG, "No GameType found for game ID: ${definition.id}")
                return null
            }

            val score = gameType.scoreField.invoke(scores)
            Log.d(TAG, "High score for ${gameType.name}: $score")

            // Log all scores for debugging
            Log.d(TAG, "All scores: $scores")

            score
        } catch (e: Exception) {
            Log.e(TAG, "Error getting high score for ${definition.id}", e)
            null
        }
    }

    private fun getMathWriteStats(
        definition: GameDefinition,
        stats: Map<String, MathWriteGameStats>
    ): MathWriteGameStats? {
        return stats[definition.id]
    }
}
