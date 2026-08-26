package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import org.junit.Assert.assertEquals
import org.junit.Test

class GameRouteResolverTest {

    @Test
    fun `resolves parameterized write game route`() {
        val route = GameRouteResolver.resolve(GameTypes.SUM_WRITE)

        assertEquals(MathWriteRoute(GameTypes.SUM_WRITE.id), route)
    }

    @Test
    fun `resolves simple game route`() {
        val route = GameRouteResolver.resolve(GameTypes.MEMORY_FLASH)

        assertEquals(MemoryFlashRoute, route)
    }
}
