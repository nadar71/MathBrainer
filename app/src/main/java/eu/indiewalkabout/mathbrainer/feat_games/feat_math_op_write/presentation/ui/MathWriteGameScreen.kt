package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui

// Write Result presents an arithmetic expression and asks the player to type the correct answer.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard.Keypad
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.HeaderInfo
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.WriteChallengeCard

@Composable
fun MathWriteGameScreen(
    operation: String,
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: MathOpWriteResultViewModel = hiltViewModel()
) {
    val gameId = operation

    LaunchedEffect(gameId, initialHighScore) {
        viewModel.initialize(gameId, initialHighScore)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // ----------------------------------------- UI ------------------------------------------------
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                IconButton(onClick = {
                    viewModel.onBackPressed()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_back),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.Companion.padding(4.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.math_write_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.math_write_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderInfo(
                state = state,
                levelChallengesCompleted = state.challengesCompleted,
                levelChallengesTarget = state.challengesPerLevel
                )
            WriteChallengeCard(state = state)
            Keypad(
                feedback = state.feedback,
                inputValue = state.inputValue,
                onDigitPressed = { digit -> viewModel.onDigitPressed(digit) },
                onDelete = { viewModel.onDeletePressed() },
                onSubmit = { viewModel.onSubmitPressed() }
            )
        }
    }

    if (state.isGameOver) {
        GameOverDialog(onDismiss = {
            viewModel.onBackPressed()
            onBack()
        })
    }
}
