package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model

data class MathChooseChallenge(
    val firstOperand: Int,
    val secondOperand: Int,
    val operation: Char,
    val correctAnswer: Int,
    val options: List<Int>
)