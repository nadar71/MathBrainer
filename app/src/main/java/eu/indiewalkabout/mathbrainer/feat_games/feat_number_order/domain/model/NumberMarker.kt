package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model

import androidx.compose.ui.geometry.Offset

data class NumberMarker(
    val index: Int,
    val center: Offset,
    val radius: Float
)