package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteGameStats

data class GameUiModel(
    val definition: GameDefinition,
    val highScore: Int? = null,
    val mathWriteStats: MathWriteGameStats? = null
)