package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import eu.indiewalkabout.mathbrainer.feat_home.presentation.model.GameUiModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val games: List<GameUiModel> = emptyList()
)
