package eu.indiewalkabout.mathbrainer.feat_home.presentation.model

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition

data class GameUiModel(
    val definition: GameDefinition,
    val highScore: Int? = null
)
