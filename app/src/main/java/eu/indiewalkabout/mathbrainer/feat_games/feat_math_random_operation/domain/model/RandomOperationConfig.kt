package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model

data class RandomOperationConfig(
    val min: Int,
    val max: Int,
    val multMin: Int,
    val multLowMax: Int,
    val multHighMax: Int,
    val divMin: Int,
    val divLowMax: Int,
    val divHighMax: Int,
    val level: Int
)
