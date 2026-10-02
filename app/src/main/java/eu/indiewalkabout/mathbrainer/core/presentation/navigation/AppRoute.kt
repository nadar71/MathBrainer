package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey

@Serializable
data object HomeRoute : AppRoute

@Serializable
data object StatisticsRoute : AppRoute

@Serializable
data object GameSettingsRoute : AppRoute

@Serializable
data object GameCreditsRoute : AppRoute

@Serializable
data class MathWriteRoute(val operation: String) : AppRoute

@Serializable
data class MathChooseRoute(val operation: String) : AppRoute

@Serializable
data object DoubleNumberRoute : AppRoute

@Serializable
data object RandomOperationRoute : AppRoute

@Serializable
data object MemoryFlashRoute : AppRoute

@Serializable
data object CountObjectsRoute : AppRoute

@Serializable
data object NumberOrderRoute : AppRoute

@Serializable
data object SequenceCompleteRoute : AppRoute

@Serializable
data object FallingOpsRoute : AppRoute

@Serializable
data object EnigmaRoute : AppRoute
