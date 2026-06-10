package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class DoubleNumberChallenge(
    val value: Int,
    val answer: Int
): Challenge
