package eu.indiewalkabout.mathbrainer.core.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_ads.presentation.AdMobBannerView
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui.EnigmaGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui.FallingOpsGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui.MathChooseGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui.DoubleNumberGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui.RandomOperationGameScreen
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.ui.MemoryFlashGameScreen
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
            ScreenWithBottomBanner {
                HomeScreen(
                    navController = navController,
                    homeViewModel = viewModel
                )
            }
        }

        // --- MathWrite Game ---
        composable(
            route = ScreenRoutes.MathWriteGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = URLDecoder.decode(encodedOperation, "UTF-8")

            ScreenWithBottomBanner {
                MathWriteGameScreen(
                    operation = operation,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- MathChoose Game ---
        composable(
            route = ScreenRoutes.MathChooseGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = java.net.URLDecoder.decode(encodedOperation, "UTF-8")

            ScreenWithBottomBanner {
                MathChooseGameScreen(
                    operation = operation,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Double Number Game ---
        composable(route = ScreenRoutes.DoubleNumberGame.route) {
            ScreenWithBottomBanner {
                DoubleNumberGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Random Operation Game ---
        composable(route = ScreenRoutes.RandomOperationGame.route) {
            ScreenWithBottomBanner {
                RandomOperationGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Memory Flash Game ---
        composable(route = ScreenRoutes.MemoryFlashGame.route) {
            ScreenWithBottomBanner {
                MemoryFlashGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Number Order Game ---
        composable(route = ScreenRoutes.NumberOrderGame.route) {
            ScreenWithBottomBanner {
                NumberOrderGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Count Objects Game ---
        composable(route = ScreenRoutes.CountObjectsGame.route) {
            ScreenWithBottomBanner {
                CountObjectsGameScreen(onBack = { navController.popBackStack() })
            }
        }

        // --- Sequence Completion Game ---
        composable(route = ScreenRoutes.SequenceCompleteGame.route) {
            ScreenWithBottomBanner {
                SequenceCompleteGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Falling Operations Game ---
        composable(route = ScreenRoutes.FallingOpsGame.route) {
            ScreenWithBottomBanner {
                FallingOpsGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Enigma Game ---
        composable(route = ScreenRoutes.EnigmaGame.route) {
            ScreenWithBottomBanner {
                EnigmaGameScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- Settings ---
        composable(ScreenRoutes.GameSettings.route) {
            ScreenWithBottomBanner {
                GameSettingsScreen(
                    onBack = { navController.popBackStack() },
                    onCreditsClick = { navController.navigate(ScreenRoutes.GameCredits.route) }
                )
            }
        }
        
        // --- Credits ---
        composable(ScreenRoutes.GameCredits.route) {
            ScreenWithBottomBanner {
                GameCreditsScreen(onBack = { navController.popBackStack() })
            }
        }

        // --- Statistics ---
        composable(ScreenRoutes.Statistics.route) {
            val viewModel = hiltViewModel<StatisticViewModel>()
            ScreenWithBottomBanner {
                StatisticScreen(navController = navController, statisticViewModel = viewModel)
            }
        }
    }
}

@Composable
private fun ScreenWithBottomBanner(content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f, fill = true)) {
            content()
        }
        AdMobBannerView(
            adUnitId = stringResource(R.string.admob_key_bottom_banner),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
