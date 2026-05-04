package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes

object GameRouteResolver {

    fun resolve(gameType: GameTypes): String {
        return when (gameType) {
            GameTypes.SUM_WRITE,
            GameTypes.DIFF_WRITE,
            GameTypes.MULT_WRITE,
            GameTypes.DIV_WRITE,
            GameTypes.MIX_WRITE -> ScreenRoutes.MathWriteGame.createRoute(gameType.id)

            GameTypes.SUM_CHOOSE,
            GameTypes.DIFF_CHOOSE,
            GameTypes.MULT_CHOOSE,
            GameTypes.DIV_CHOOSE,
            GameTypes.MIX_CHOOSE -> ScreenRoutes.MathChooseGame.createRoute(gameType.id)

            GameTypes.RANDOM_OPERATION -> ScreenRoutes.RandomOperationGame.route
            GameTypes.MEMORY_FLASH -> ScreenRoutes.MemoryFlashGame.route
            GameTypes.DOUBLE_NUMBER -> ScreenRoutes.DoubleNumberGame.route
            GameTypes.QUICK_COUNT -> ScreenRoutes.CountObjectsGame.route
            GameTypes.NUMBER_ORDER -> ScreenRoutes.NumberOrderGame.route
            GameTypes.SEQUENCE_COMPLETE -> ScreenRoutes.SequenceCompleteGame.route
            GameTypes.FALLING_OPS -> ScreenRoutes.FallingOpsGame.route
            GameTypes.ENIGMA -> ScreenRoutes.EnigmaGame.route
        }
    }
}
