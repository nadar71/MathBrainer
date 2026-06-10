package eu.indiewalkabout.mathbrainer.feat_home.presentation.state

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel


data class HomeUiState(
    val isLoading: Boolean = false,
    val games: List<GameUiModel> = emptyList(),
    val error: String? = null
)