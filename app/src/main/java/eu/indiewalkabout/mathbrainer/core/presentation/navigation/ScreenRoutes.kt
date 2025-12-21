package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import java.net.URLEncoder

sealed class ScreenRoutes(val route: String) {
    object Home : ScreenRoutes("home")
    object MathWriteGame : ScreenRoutes("math_write_game/{operation}/{highScore}") {
        fun createRoute(operation: String, highScore: Int = 0): String {
            val encodedOperation = URLEncoder.encode(operation, "UTF-8")
            return "math_write_game/$encodedOperation/$highScore"
        }
    }
    object MathChooseGame : ScreenRoutes("math_choose_game/{operation}/{highScore}") {
        fun createRoute(operation: String, highScore: Int = 0): String {
            val encodedOperation = URLEncoder.encode(operation, "UTF-8")
            return "math_choose_game/$encodedOperation/$highScore"
        }
    }
    object DoubleNumberGame : ScreenRoutes("double_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String = "double_game/$highScore"
    }
    object RandomOperationGame : ScreenRoutes("random_op_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String = "random_op_game/$highScore"
    }
    object CountObjectsGame : ScreenRoutes("count_objects_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String {
            return "count_objects_game/$highScore"
        }
    }
    object NumberOrderGame : ScreenRoutes("number_order_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String = "number_order_game/$highScore"
    }
    object SequenceCompleteGame : ScreenRoutes("sequence_complete_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String = "sequence_complete_game/$highScore"
    }
    object FallingOpsGame : ScreenRoutes("falling_ops_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String = "falling_ops_game/$highScore"
    }
    object GameSettings : ScreenRoutes("game_settings")
    object GameCredits : ScreenRoutes("game_credits")
    object Statistics : ScreenRoutes("statistics")

}
