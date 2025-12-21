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
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui.EnigmaGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui.DoubleNumberGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui.RandomOperationGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui.NumberOrderGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.ui.SequenceCompleteGameScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeViewModel
import eu.indiewalkabout.mathbrainer.feat_settings.presentation.ui.GameSettingsScreen
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.StatisticScreen
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.StatisticViewModel
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

        // --- Double Number Game ---
        composable(
            route = ScreenRoutes.DoubleNumberGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            DoubleNumberGameScreen(
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Random Operation Game ---
        composable(
            route = ScreenRoutes.RandomOperationGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            RandomOperationGameScreen(
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Number Order Game ---
        composable(
            route = ScreenRoutes.NumberOrderGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            NumberOrderGameScreen(
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

        // --- Sequence Completion Game ---
        composable(
            route = ScreenRoutes.SequenceCompleteGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            SequenceCompleteGameScreen(
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Enigma Game ---
        composable(
            route = ScreenRoutes.EnigmaGame.route,
            arguments = listOf(
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            EnigmaGameScreen(
                initialHighScore = highScore,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Settings ---
        composable(ScreenRoutes.GameSettings.route) {
            GameSettingsScreen(
                onBack = { navController.popBackStack() },
                onCreditsClick = { navController.navigate(ScreenRoutes.GameCredits.route) }
            )
        }
        
        // --- Credits ---
        composable(ScreenRoutes.GameCredits.route) {
            GameCreditsScreen(onBack = { navController.popBackStack() })
        }

        // --- Statistics ---
        composable(ScreenRoutes.Statistics.route) {
            val viewModel = hiltViewModel<StatisticViewModel>()
            StatisticScreen(navController = navController, statisticViewModel = viewModel)
        }
    }
}
