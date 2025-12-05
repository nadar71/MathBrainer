package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import androidx.annotation.StringRes

data class GameDefinition(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val requiresHighScore: Boolean = false
)
