package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class RandomOperationChallenge(
    val firstOperand: Int,
    val secondOperand: Int,
    val result: Int,
    val correctOperation: Char
): Challenge