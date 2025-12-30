package eu.indiewalkabout.mathbrainer.navigation

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
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.MathWriteGameScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeScreen
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeViewModel

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
        composable(ScreenRoutes.Home.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            ScreenWithBottomBanner {
                HomeScreen(
                    navController = navController,
                    homeViewModel = viewModel
                )
            }
        }

        composable(
            route = ScreenRoutes.MathWriteGame.route,
            arguments = listOf(
                navArgument("operation") { type = NavType.StringType },
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedOperation = backStackEntry.arguments?.getString("operation") ?: "+"
            val operation = java.net.URLDecoder.decode(encodedOperation, "UTF-8")
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0

            ScreenWithBottomBanner {
                MathWriteGameScreen(
                    operation = operation,
                    initialHighScore = highScore,
                    onBack = { navController.popBackStack() }
                )
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
