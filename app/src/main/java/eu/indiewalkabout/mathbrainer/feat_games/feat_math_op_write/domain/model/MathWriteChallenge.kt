package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model

data class MathWriteChallenge(
    val firstOperand: Int,
    val secondOperand: Int,
    val operation: Char,
    val answer: Int
)