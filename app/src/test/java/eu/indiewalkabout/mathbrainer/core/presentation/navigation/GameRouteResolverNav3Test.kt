package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import org.junit.Assert.assertEquals
import org.junit.Test

class GameRouteResolverNav3Test {

    @Test
    fun `write game types resolve to typed write routes`() {
        val route = GameRouteResolver.resolve(GameTypes.SUM_WRITE)

        assertEquals(MathWriteRoute(GameTypes.SUM_WRITE.id), route)
    }
}
