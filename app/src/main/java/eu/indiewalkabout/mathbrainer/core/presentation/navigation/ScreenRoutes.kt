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
    object CountObjectsGame : ScreenRoutes("count_objects_game/{highScore}") {
        fun createRoute(highScore: Int = 0): String {
            return "count_objects_game/$highScore"
        }
    }

}
