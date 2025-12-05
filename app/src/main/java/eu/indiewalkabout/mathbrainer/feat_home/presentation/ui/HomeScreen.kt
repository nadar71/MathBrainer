package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.ScreenRoutes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.GameGrid
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.LoadingContent
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighscoresActivity

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.Companion
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = stringResource(id = R.string.home_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

    ) { padding ->
        when {
            state.isLoading -> LoadingContent(padding)
            else -> GameGrid(
                padding = padding,
                games = state.games,
                onGameSelected = { game ->
                    when (val gameType = GameTypes.fromId(game.definition.id)) {
                        GameTypes.SUM_WRITE, GameTypes.DIFF_WRITE, GameTypes.MULT_WRITE, GameTypes.DIV_WRITE, GameTypes.MIX_WRITE -> {
                            navController.navigate(
                                ScreenRoutes.MathWriteGame.createRoute(
                                    operation = game.definition.id,
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        GameTypes.SUM_CHOOSE, GameTypes.DIFF_CHOOSE, GameTypes.MULT_CHOOSE, GameTypes.DIV_CHOOSE, GameTypes.MIX_CHOOSE -> {
                            navController.navigate(
                                ScreenRoutes.MathChooseGame.createRoute(
                                    operation = game.definition.id,
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        GameTypes.QUICK_COUNT -> {
                            navController.navigate(
                                ScreenRoutes.CountObjectsGame.createRoute(
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        GameTypes.DOUBLE_NUMBER -> {
                            navController.navigate(
                                ScreenRoutes.DoubleNumberGame.createRoute(
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        GameTypes.NUMBER_ORDER -> {
                            navController.navigate(
                                ScreenRoutes.NumberOrderGame.createRoute(
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        GameTypes.RANDOM_OPERATION -> {
                            navController.navigate(
                                ScreenRoutes.RandomOperationGame.createRoute(
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        null -> navController.navigate(ScreenRoutes.Home.route)
                    }
                },
                onHighscoresSelected = {
                    context.startActivity(Intent(context, HighscoresActivity::class.java))
                },
                onCreditsSelected = {
                    navController.navigate(ScreenRoutes.GameSettings.route)
                }
            )

        }
    }
}
