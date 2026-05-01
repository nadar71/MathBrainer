package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.state

import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.model.FallingOperationDefinition

data class FallingOperationItem(
    val id: Int,
    val definition: FallingOperationDefinition,
    val xPosition: Float,
    val progress: Float
)
