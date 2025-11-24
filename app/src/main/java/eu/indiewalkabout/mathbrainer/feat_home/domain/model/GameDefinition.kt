package eu.indiewalkabout.mathbrainer.feat_home.domain.model

import android.app.Activity
import androidx.annotation.StringRes
import kotlin.reflect.KClass

data class GameDefinition(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val target: KClass<out Activity>,
    val operation: String? = null,
    val requiresHighScore: Boolean = false
)
