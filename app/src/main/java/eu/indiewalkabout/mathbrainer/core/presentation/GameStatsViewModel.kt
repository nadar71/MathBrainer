package eu.indiewalkabout.mathbrainer.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_home.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameStatsViewModel @Inject constructor(
    private val getGameStatsUseCase: GetGameStatsUseCase
) : ViewModel() {

    private val _gameStats = MutableStateFlow<Map<String, GameStats>>(emptyMap())
    val gameStats: StateFlow<Map<String, GameStats>> = _gameStats.asStateFlow()

    private var statsJob: Job? = null

    fun refresh() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            getGameStatsUseCase().collect { stats ->
                _gameStats.value = stats
            }
        }
    }
}
