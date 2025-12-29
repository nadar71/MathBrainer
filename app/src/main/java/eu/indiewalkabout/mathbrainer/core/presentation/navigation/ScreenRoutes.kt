package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import java.net.URLEncoder

sealed class ScreenRoutes(val route: String) {
    object Home : ScreenRoutes("home")
    object MathWriteGame : ScreenRoutes("math_write_game/{operation}") {
        fun createRoute(operation: String): String {
            val encodedOperation = URLEncoder.encode(operation, "UTF-8")
            return "math_write_game/$encodedOperation"
        }
    }
    object MathChooseGame : ScreenRoutes("math_choose_game/{operation}") {
        fun createRoute(operation: String): String {
            val encodedOperation = URLEncoder.encode(operation, "UTF-8")
            return "math_choose_game/$encodedOperation"
        }
    }
    object DoubleNumberGame : ScreenRoutes("double_game")
    object RandomOperationGame : ScreenRoutes("random_op_game")
    object MemoryFlashGame : ScreenRoutes("memory_flash_game")
    object CountObjectsGame : ScreenRoutes("count_objects_game")
    object NumberOrderGame : ScreenRoutes("number_order_game")
    object SequenceCompleteGame : ScreenRoutes("sequence_complete_game")
    object FallingOpsGame : ScreenRoutes("falling_ops_game")
    object EnigmaGame : ScreenRoutes("enigma_game")
    object GameSettings : ScreenRoutes("game_settings")
    object GameCredits : ScreenRoutes("game_credits")
    object Statistics : ScreenRoutes("statistics")

}
