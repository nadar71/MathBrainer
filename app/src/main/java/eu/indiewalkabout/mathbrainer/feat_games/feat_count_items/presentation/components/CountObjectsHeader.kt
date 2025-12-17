package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.state.CountObjectsUiState

@Composable
fun CountObjectsHeader(
    state: CountObjectsUiState,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First row with level and lives
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.level_with_value, state.level),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Level progress bar
                LinearProgressIndicator(
                    progress = if (state.challengesPerLevel > 0) {
                        state.challengesCompleted.toFloat() / state.challengesPerLevel.toFloat()
                    } else 0f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer
                )

                Text(
                    text = stringResource(id = R.string.lives_with_value, state.lives),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Second row with score and high score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.score_with_value, state.score),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                state.highScore?.let { highScore ->
                    Text(
                        text = stringResource(id = R.string.highscore_with_value, highScore),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }


            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.quick_count_instructions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountObjectsHeaderPreview() {
    MathBrainerTheme {
        CountObjectsHeader(
            state = CountObjectsUiState(
                level = 1,
                score = 0,
                lives = 3,
                highScore = 100,
                challengesCompleted = 3,
                challengesPerLevel = 5,
                isShowingItems = true
            )
        )
    }
}
