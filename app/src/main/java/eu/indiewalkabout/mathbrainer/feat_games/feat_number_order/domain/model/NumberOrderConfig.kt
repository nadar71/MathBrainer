package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model

data class NumberOrderConfig(
    val maxItemsToCount: Float,
    val minRatio: Double = 0.7
)