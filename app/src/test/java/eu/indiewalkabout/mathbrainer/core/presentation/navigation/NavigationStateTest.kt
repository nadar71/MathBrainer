package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationStateTest {

    @Test
    fun `navigation state starts from home route`() {
        val state = NavigationState(
            backStack = NavBackStack(HomeRoute)
        )

        assertEquals(HomeRoute, state.backStack.first())
        assertEquals(HomeRoute, state.currentRoute)
    }
}
