package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsActivity
import eu.indiewalkabout.mathbrainer.feat_home.presentation.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighscoresActivity

@AndroidEntryPoint
class HomeGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathBrainerTheme {
                HomeScreen()
            }
        }
    }

    companion object {
        const val OPERATION_KEY = "operation"
        const val HIGHSCORE = "highscore"
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
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
                    val intent = buildIntentForGame(game, context)
                    context.startActivity(intent)
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

@Composable
private fun LoadingContent(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun GameGrid(
    padding: PaddingValues,
    games: List<GameUiModel>,
    onGameSelected: (GameUiModel) -> Unit,
    onHighscoresSelected: () -> Unit,
    onCreditsSelected: () -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.home_progress_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.home_progress_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(id = R.string.highscores_title),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onHighscoresSelected() }
                        )
                        Text(
                            text = stringResource(id = R.string.credits_title_short),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onCreditsSelected() }
                        )
                    }
                }
            }
        }

        items(games) { game ->
            GameCard(gameUiModel = game, onGameSelected = onGameSelected)
        }
    }
}

@Composable
private fun GameCard(
    gameUiModel: GameUiModel,
    onGameSelected: (GameUiModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGameSelected(gameUiModel) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = gameUiModel.definition.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = gameUiModel.definition.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            gameUiModel.highScore?.let { score ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.highscore_with_value, score),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun buildIntentForGame(gameUiModel: GameUiModel, context: Context): Intent {
    val intent = Intent(context, gameUiModel.definition.target.java)
    gameUiModel.definition.operation?.let { operation ->
        intent.putExtra(HomeGameActivity.OPERATION_KEY, operation)
    }
    if (gameUiModel.definition.requiresHighScore) {
        gameUiModel.highScore?.let { highScore ->
            intent.putExtra(HomeGameActivity.HIGHSCORE, highScore)
        }
    }
    return intent
}
