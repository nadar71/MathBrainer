package eu.indiewalkabout.mathbrainer.feat_home.presentation.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel

@Composable
fun GameGrid(
    padding: PaddingValues,
    games: List<GameUiModel>,
    onGameSelected: (GameUiModel) -> Unit,
    onHighscoresSelected: () -> Unit,
    onCreditsSelected: () -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(padding),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Titling spanned card
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.Companion.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.home_progress_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.Companion.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.home_progress_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.Companion.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = stringResource(id = R.string.highscores_title),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Companion.Bold,
                            modifier = Modifier.Companion.clickable { onHighscoresSelected() }
                        )
                        Text(
                            text = stringResource(id = R.string.credits_title_short),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Companion.Bold,
                            modifier = Modifier.Companion.clickable { onCreditsSelected() }
                        )
                    }
                }
            }
        }

        // games card on 3 columns
        items(games) { game ->
            GameCard(gameUiModel = game, onGameSelected = onGameSelected)
        }
    }
}