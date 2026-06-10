package eu.indiewalkabout.mathbrainer.feat_home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameDefinition
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats

@Composable
fun GameCard(
    gameUiModel: GameUiModel,
    onGameSelected: (GameUiModel) -> Unit,
    showMinimal: Boolean = false
) {
    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable { onGameSelected(gameUiModel) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Image (if provided)
            gameUiModel.definition.imageResId?.let { imageResId ->
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 4.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // Title (if provided)
            gameUiModel.definition.titleRes?.let { titleRes ->
                Text(
                    text = stringResource(id = titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Always show win percentage and best level, even if stats are null
            val stats = gameUiModel.gameStats
            val winPercentage = if (stats == null || stats.challengesPlayed == 0 || stats.challengesWon == 0) {
                0
            } else {
                minOf(100, ((stats.challengesWon.toDouble() / stats.challengesPlayed) * 100).toInt())
            }

            // Show win percentage with bulb symbol and conditional coloring
            // - 0%: White
            // - 1-49%: Error color (red)
            // - 50-100%: Tertiary color (green)
            val textColor = when {
                winPercentage == 0 -> MaterialTheme.colorScheme.onSurface // White for 0%
                winPercentage < 50 -> MaterialTheme.colorScheme.error // Error color for 1-49%
                winPercentage >= 50 && winPercentage < 100 -> MaterialTheme.colorScheme.tertiary// Green for >=50% <100%
                else -> MaterialTheme.colorScheme.primary // yellow for 100%
            }

            // Show win percentage and best level in the same row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = if (winPercentage > 0) "W ${winPercentage}%" else " ",
                    style = TextStyle(
                        color = textColor,
                        fontSize = 10.sp,
                    ),
                    color = textColor
                )

                val bestLevel = stats?.lastLevel ?: 0
                Text(
                    text = if (bestLevel > 0) "L ${bestLevel}" else " ",
                    style = TextStyle(
                        color = textColor,
                        fontSize = 10.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

            // Only show additional stats if not in minimal mode
            if (!showMinimal) {
                val hasHighScore = gameUiModel.highScore != null
                val scoreText = if (hasHighScore) {
                    stringResource(id = R.string.highscore_with_value, gameUiModel.highScore)
                } else {
                    stringResource(id = R.string.no_high_score)
                }

                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (hasHighScore) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (hasHighScore) {
                        if ((gameUiModel.highScore ?: 0) > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        }
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )

                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = stringResource(id = R.string.game_played_count, stats?.challengesPlayed ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = R.string.game_win_loss, stats?.challengesWon ?: 0, stats?.challengesLost ?: 0),
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
                highScore = 100,
                gameStats = GameStats(
                    gameId = GameTypes.MULT_WRITE.id,
                    highScore = 100,
                    challengesPlayed = 10,
                    challengesWon = 7,
                    challengesLost = 3,
                    lastLevel = 5
                )
            ),
            onGameSelected = {},
            showMinimal = true
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
