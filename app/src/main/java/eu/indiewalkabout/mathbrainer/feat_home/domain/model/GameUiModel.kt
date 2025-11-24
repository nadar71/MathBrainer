package eu.indiewalkabout.mathbrainer.feat_home.domain.model

data class GameUiModel(
    val definition: GameDefinition,
    val highScore: Int? = null
)