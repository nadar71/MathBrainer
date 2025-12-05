package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_home.data.local.gamesDefinitionsList
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetGameScoresUseCase
import eu.indiewalkabout.mathbrainer.feat_home.presentation.state.HomeUiState
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameScores
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGameScoresUseCase: GetGameScoresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var scoresJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        scoresJob?.cancel()
        scoresJob = viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            getGameScoresUseCase().collect { scores ->
                val games = gamesDefinitionsList.map { definition ->
                    GameUiModel(
                        definition = definition,
                        highScore = getHighScore(definition, scores)
                    )
                }
                _uiState.value = HomeUiState(isLoading = false, games = games)
            }
        }
    }

    private fun getHighScore(definition: GameDefinition, scores: GameScores): Int? {
        return GameTypes.fromId(definition.id)?.scoreField?.invoke(scores)
    }
}
