package eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.ScreenRoutes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.GameGrid
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.LoadingContent

@Composable
fun StatisticScreen(
    navController: NavHostController,
    statisticViewModel: StatisticViewModel = hiltViewModel()
) {
    val state by statisticViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp)
                        .clickable(onClick = { navController.popBackStack() })
                )
            }
        },

        ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.statistics_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )
            when {
                state.isLoading -> {
                    LoadingContent(padding)
                }

                !state.error.isNullOrEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error loading scores: ${state.error}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { statisticViewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    GameGrid(
                        modifier = Modifier.padding(top = 16.dp),
                        paddingValues = padding,
                        games = state.games,
                        showMinimal = false,
                        onGameSelected = { game ->
                            Log.d("StatisticScreen", "Game selected: ${game.definition.id}")
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

                                GameTypes.RANDOM_OPERATION -> {
                                    navController.navigate(
                                        ScreenRoutes.RandomOperationGame.createRoute(
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

                                GameTypes.QUICK_COUNT -> {
                                    navController.navigate(
                                        ScreenRoutes.CountObjectsGame.createRoute(
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

                                GameTypes.SEQUENCE_COMPLETE -> {
                                    navController.navigate(
                                        ScreenRoutes.SequenceCompleteGame.createRoute(
                                            highScore = game.highScore ?: 0
                                        )
                                    )
                                }

                                GameTypes.FALLING_OPS -> {
                                    navController.navigate(
                                        ScreenRoutes.FallingOpsGame.createRoute(
                                            highScore = game.highScore ?: 0
                                        )
                                    )
                                }

                                GameTypes.ENIGMA -> {
                                    navController.navigate(
                                        ScreenRoutes.EnigmaGame.createRoute(
                                            highScore = game.highScore ?: 0
                                        )
                                    )
                                }

                                null -> navController.navigate(ScreenRoutes.Home.route)
                            }
                        }
                    )
                }

            }
        }
    }
}
