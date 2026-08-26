package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class Navigator(
    private val navigationState: NavigationState
) {
    fun navigate(route: AppRoute) {
        navigationState.backStack.add(route)
    }

    fun back() {
        if (navigationState.backStack.size > 1) {
            navigationState.backStack.removeAt(navigationState.backStack.lastIndex)
        }
    }
}

@Composable
fun rememberNavigator(
    navigationState: NavigationState
): Navigator = remember(navigationState) {
    Navigator(navigationState)
}
