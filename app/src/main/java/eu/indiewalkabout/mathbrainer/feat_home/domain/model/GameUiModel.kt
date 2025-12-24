package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats

data class GameUiModel(
    val definition: GameDefinition,
    val highScore: Int? = null,
    val gameStats: GameStats? = null
)