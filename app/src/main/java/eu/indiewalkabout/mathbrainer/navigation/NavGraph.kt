package eu.indiewalkabout.mathbrainer.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MathWriteGame : Screen("math_write_game/{operation}/{highScore}") {
        fun createRoute(operation: String, highScore: Int = 0): String {
            val encodedOperation = java.net.URLEncoder.encode(operation, "UTF-8")
            return "math_write_game/$encodedOperation/$highScore"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    // Log navigation events
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("Navigation", "Navigated to: ${destination.route}")
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navController = navController,
                homeViewModel = viewModel
            )
        }
        
        composable(
            route = Screen.MathWriteGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType },
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = java.net.URLDecoder.decode(encodedOperation, "UTF-8")
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0
            
            MathWriteGameScreen(
                operation = operation,
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
