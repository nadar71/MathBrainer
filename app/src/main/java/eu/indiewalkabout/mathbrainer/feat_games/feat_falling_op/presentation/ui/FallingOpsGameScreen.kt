package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui

// Falling Operations challenges the player to solve operations under pressure as prompts move dynamically.

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.GameOverDialog
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.components.FallingOpsHeader
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.components.FallingOpsKeypad
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FallingOpsGameScreen(
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: FallingOpsViewModel = hiltViewModel(),
) {
    val gameId = GameTypes.FALLING_OPS.id
    val gameStats by viewModel.gameStats.collectAsState()
    var hasStarted by remember(gameId) { mutableStateOf(false) }

    LaunchedEffect(gameId, initialHighScore) {
        viewModel.refreshGameStat(gameId, initialHighScore)
    }

    LaunchedEffect(gameId, gameStats) {
        if (!hasStarted && gameStats != null) {
            viewModel.startGame()
            hasStarted = true
        }
    }

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.onQuitGame()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.falling_ops_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.falling_ops_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FallingOpsHeader(state = state)

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val density = LocalDensity.current
                val rectWidth = 120.dp
                val rectHeight = 48.dp
                val maxWidthPx = with(density) { (maxWidth - rectWidth).toPx().coerceAtLeast(0f) }
                val maxHeightPx = with(density) { (maxHeight - rectHeight).toPx().coerceAtLeast(0f) }

                state.operations.forEach { item ->
                    val xOffset = (item.xPosition * maxWidthPx).roundToInt()
                    val yOffset = (item.progress.coerceIn(0f, 1f) * maxHeightPx).roundToInt()
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier
                            .offset { IntOffset(xOffset, yOffset) }
                            .width(rectWidth)
                            .height(rectHeight)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.definition.expression,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                )
            }

            Text(
                text = stringResource(id = R.string.current_input, state.input.ifEmpty { "" }),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            /*when (state.feedback) {
                ChallengeUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.ok_str),
                    color = MaterialTheme.colorScheme.primary
                )

                ChallengeUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.wrong_answer),
                    color = MaterialTheme.colorScheme.error
                )

                null -> {}
            }*/

            FallingOpsKeypad(
                feedback = state.feedback,
                inputValue = state.input,
                onDigitPressed = { viewModel.onDigitPressed(it) },
                onDelete = { viewModel.onDelete() },
                onSubmit = { viewModel.onSubmit() }
            )
        }
    }

    if (state.isGameOver) {
        GameOverDialog(onDismiss = {
            viewModel.onQuitGame()
            onBack()
        })
    }
}
