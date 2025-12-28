package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class MathChooseChallenge(
    val firstOperand: Int,
    val secondOperand: Int,
    val operation: Char,
    val correctAnswer: Int,
    val options: List<Int>
): Challenge