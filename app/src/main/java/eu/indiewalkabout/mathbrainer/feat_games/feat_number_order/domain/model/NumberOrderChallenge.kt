package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class NumberOrderChallenge(
    val itemCount: Int,
    val placementSeed: Int
): Challenge