package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigatorTest {

    @Test
    fun `navigate appends destination to back stack`() {
        val state = NavigationState(NavBackStack(HomeRoute))
        val navigator = Navigator(state)

        navigator.navigate(StatisticsRoute)

        assertEquals(listOf(HomeRoute, StatisticsRoute), state.backStack.toList())
    }

    @Test
    fun `back removes top route when possible`() {
        val state = NavigationState(NavBackStack(HomeRoute, StatisticsRoute))
        val navigator = Navigator(state)

        navigator.back()

        assertEquals(listOf(HomeRoute), state.backStack.toList())
    }
}
