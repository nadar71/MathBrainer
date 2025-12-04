package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import MathChooseGameScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeViewModel
import java.net.URLDecoder

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = ScreenRoutes.Home.route
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

        // --- Home ---
        composable(ScreenRoutes.Home.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navController = navController,
                homeViewModel = viewModel
            )
        }

        // --- MathWrite Game ---
        composable(
            route = ScreenRoutes.MathWriteGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType },
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = URLDecoder.decode(encodedOperation, "UTF-8")
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0
            
            MathWriteGameScreen(
                operation = operation,
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- MathChoose Game ---
        composable(
            route = ScreenRoutes.MathChooseGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType },
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = java.net.URLDecoder.decode(encodedOperation, "UTF-8")
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            MathChooseGameScreen(
                operation = operation,
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Count Objects Game ---
        composable(
            route = ScreenRoutes.CountObjectsGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0
            CountObjectsGameScreen(
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
