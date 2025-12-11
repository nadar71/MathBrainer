package eu.indiewalkabout.mathbrainer.feat_home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun GameCard(
    gameUiModel: GameUiModel,
    onGameSelected: (GameUiModel) -> Unit
) {
    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable { onGameSelected(gameUiModel) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            Text(
                text = stringResource(id = gameUiModel.definition.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.Companion.height(4.dp))
            Text(
                text = stringResource(id = gameUiModel.definition.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            // Always show the score, even if it's 0
            val hasHighScore = gameUiModel.highScore != null
            val scoreText = if (hasHighScore) {
                stringResource(id = R.string.highscore_with_value, gameUiModel.highScore!!)
            } else {
                stringResource(id = R.string.no_high_score)
            }
            
            Text(
                text = scoreText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (hasHighScore) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (hasHighScore) {
                    if (gameUiModel.highScore > 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                }
            )

            gameUiModel.mathWriteStats?.let { stats ->
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = stringResource(id = R.string.math_write_played_count, stats.gamesPlayed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = R.string.math_write_win_loss, stats.gamesWon, stats.gamesLost),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = R.string.math_write_best_level, stats.lastLevel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun GameCardPreview() {
    GameCard(
        gameUiModel = GameUiModel(
            definition = GameDefinition(
                titleRes = R.string.game_title,
                descriptionRes = R.string.game_description,
                highScore = 100
            ),
            highScore = 100
        ),
        onGameSelected = {}
    )
}*/
