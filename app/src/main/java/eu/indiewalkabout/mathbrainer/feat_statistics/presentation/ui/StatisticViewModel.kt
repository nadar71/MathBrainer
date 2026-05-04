package eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_home.data.local.gamesDefinitionsList
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.BuildGameCatalogUiModelsUseCase
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetGameScoresUseCase
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_home.presentation.state.GameCatalogUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    private val getGameScoresUseCase: GetGameScoresUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val buildGameCatalogUiModelsUseCase: BuildGameCatalogUiModelsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameCatalogUiState(isLoading = true))
    val uiState: StateFlow<GameCatalogUiState> = _uiState.asStateFlow()
    private var scoresJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        scoresJob?.cancel()
        scoresJob = viewModelScope.launch {
            _uiState.value = GameCatalogUiState(isLoading = true)
            try {
                combine(
                    getGameScoresUseCase(),
                    getGameStatsUseCase()
                ) { scores, stats ->
                    Pair(scores, stats)
                }.collect { (scores, stats) ->
                    val games = buildGameCatalogUiModelsUseCase(gamesDefinitionsList, scores, stats)
                    _uiState.value = GameCatalogUiState(isLoading = false, games = games)
                }
            } catch (e: Exception) {
                _uiState.value = GameCatalogUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}
