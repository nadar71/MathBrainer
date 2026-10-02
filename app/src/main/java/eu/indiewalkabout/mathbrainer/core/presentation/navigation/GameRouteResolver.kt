package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes

object GameRouteResolver {

    fun resolve(gameType: GameTypes): AppRoute {
        return when (gameType) {
            GameTypes.SUM_WRITE,
            GameTypes.DIFF_WRITE,
            GameTypes.MULT_WRITE,
            GameTypes.DIV_WRITE,
            GameTypes.MIX_WRITE -> MathWriteRoute(gameType.id)

            GameTypes.SUM_CHOOSE,
            GameTypes.DIFF_CHOOSE,
            GameTypes.MULT_CHOOSE,
            GameTypes.DIV_CHOOSE,
            GameTypes.MIX_CHOOSE -> MathChooseRoute(gameType.id)

            GameTypes.RANDOM_OPERATION -> RandomOperationRoute
            GameTypes.MEMORY_FLASH -> MemoryFlashRoute
            GameTypes.DOUBLE_NUMBER -> DoubleNumberRoute
            GameTypes.QUICK_COUNT -> CountObjectsRoute
            GameTypes.NUMBER_ORDER -> NumberOrderRoute
            GameTypes.SEQUENCE_COMPLETE -> SequenceCompleteRoute
            GameTypes.FALLING_OPS -> FallingOpsRoute
            GameTypes.ENIGMA -> EnigmaRoute
        }
    }
}
