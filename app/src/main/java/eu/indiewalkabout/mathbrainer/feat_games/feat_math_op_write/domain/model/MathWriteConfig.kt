package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model

data class MathWriteConfig(
    val operationCode: String,
    val min: Int,
    val max: Int,
    val multMin: Int,
    val multLowMax: Int,
    val multHighMax: Int,
    val divMin: Int,
    val divLowMax: Int,
    val divHighMax: Int
)