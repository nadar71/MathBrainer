package eu.indiewalkabout.mathbrainer.navigation

import java.net.URLEncoder

sealed class ScreenRoutes(val route: String) {
    object Home : ScreenRoutes("home")
    object MathWriteGame : ScreenRoutes("math_write_game/{operation}") {
        fun createRoute(operation: String): String {
            val encodedOperation = URLEncoder.encode(operation, "UTF-8")
            return "math_write_game/$encodedOperation"
        }
    }
}