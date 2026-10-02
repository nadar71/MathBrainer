package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class GameDefinition(
    val id: String, // game type id string as defined in GamesStaticData.kt
    @StringRes val titleRes: Int? = null,
    @DrawableRes val imageResId: Int? = null,
    val requiresHighScore: Boolean = false
)
