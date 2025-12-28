package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model

import eu.indiewalkabout.mathbrainer.core.presentation.state.Challenge

data class CountObjectsChallenge(
    val itemsToCount: Int,
    val answerOptions: List<Int>
): Challenge