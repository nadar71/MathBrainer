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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
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
        Column(modifier = Modifier.Companion.padding(8.dp)) {
            Text(
                text = stringResource(id = gameUiModel.definition.titleRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            /*Spacer(modifier = Modifier.Companion.height(4.dp))
            Text(
                text = stringResource(id = gameUiModel.definition.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )*/
            Spacer(modifier = Modifier.Companion.height(4.dp))
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
                if (stats.gamesPlayed > 0) {
                    val winPercentage = if (stats.gamesWon > 0) {
                        // Calculate percentage and round up to nearest integer
                        ((stats.gamesWon.toDouble() / stats.gamesPlayed) * 100).toInt()
                    } else {
                        0
                    }
                    Text(
                        text = stringResource(id = R.string.math_write_win_percentage, winPercentage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(id = R.string.math_write_best_level, stats.lastLevel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_mult_text,
                    id = GameTypes.MULT_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SumWriteGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_sum_text,
                    id = GameTypes.SUM_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DiffWriteGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_diff_text,
                    id = GameTypes.DIFF_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultWriteGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_mult_text,
                    id = GameTypes.MULT_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DivWriteGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_div_text,
                    id = GameTypes.DIV_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MixChooseGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_choose_result_allop_text,
                    id = GameTypes.MIX_CHOOSE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MixWriteGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_write_result_allop_text,
                    id = GameTypes.MIX_WRITE.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuickCountGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.quick_count_title,
                    id = GameTypes.QUICK_COUNT.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoubleNumberGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_double_number_title,
                    id = GameTypes.DOUBLE_NUMBER.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.number_order_title,
                    id = GameTypes.NUMBER_ORDER.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RandomOperationGameCardPreview() {
    MathBrainerTheme {
        GameCard(
            gameUiModel = GameUiModel(
                definition = GameDefinition(
                    titleRes = R.string.game_card_choose_random_operation_text,
                    id = GameTypes.RANDOM_OPERATION.id,
                ),
                highScore = 100
            ),
            onGameSelected = {}
        )
    }
}
