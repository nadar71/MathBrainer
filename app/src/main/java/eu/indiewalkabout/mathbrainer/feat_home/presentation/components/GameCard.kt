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
            gameUiModel.highScore?.let { score ->
                Spacer(modifier = Modifier.Companion.height(8.dp))
                Text(
                    text = stringResource(id = R.string.highscore_with_value, score),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Companion.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}