package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model

data class CountObjectsChallenge(
    val itemsToCount: Int,
    val answerOptions: List<Int>
)