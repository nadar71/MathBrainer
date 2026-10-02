package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack

@Stable
class NavigationState(
    val backStack: NavBackStack<AppRoute>
) {
    val currentRoute: AppRoute
        get() = backStack.lastOrNull() ?: HomeRoute
}

@Composable
@Suppress("UNCHECKED_CAST")
fun rememberNavigationState(
    startRoute: AppRoute = HomeRoute
): NavigationState {
    val backStack = rememberNavBackStack(startRoute) as NavBackStack<AppRoute>
    return remember(backStack) {
        NavigationState(backStack)
    }
}
