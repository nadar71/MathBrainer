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
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsActivity
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.GameGrid
import eu.indiewalkabout.mathbrainer.feat_home.presentation.components.LoadingContent
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighscoresActivity
import eu.indiewalkabout.mathbrainer.navigation.ScreenRoutes

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
                    when (game.definition.id) {
                        "sum_write", "diff_write", "mult_write", "div_write", "mix_write" -> {
                            navController.navigate(
                                ScreenRoutes.MathWriteGame.createRoute(
                                    operation = game.definition.id,
                                    highScore = game.highScore ?: 0
                                )
                            )
                        }
                        else -> {
                            // Fallback for other game types that haven't been migrated yet
                            val intent = Intent(context, game.definition.target.java)
                            context.startActivity(intent)
                        }
                    }
                },
                onHighscoresSelected = {
                    context.startActivity(Intent(context, HighscoresActivity::class.java))
                },
                onCreditsSelected = {
                    context.startActivity(Intent(context, GameCreditsActivity::class.java))
                }
            )

        }
    }
}