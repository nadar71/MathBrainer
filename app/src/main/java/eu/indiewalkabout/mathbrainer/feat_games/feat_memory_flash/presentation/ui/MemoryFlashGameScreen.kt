package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard.Keypad
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.components.MemoryFlashChallengeCard
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.components.MemoryFlashHeader
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes

@Composable
fun MemoryFlashGameScreen(
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: MemoryFlashViewModel = hiltViewModel(),
) {
    val gameId = GameTypes.MEMORY_FLASH.id
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
                        text = stringResource(id = R.string.memory_flash_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.memory_flash_subtitle),
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MemoryFlashHeader(state = state)
            MemoryFlashChallengeCard(state = state)

            when (state.feedback) {
                ChallengeUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.memory_flash_success),
                    color = MaterialTheme.colorScheme.primary
                )

                ChallengeUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.memory_flash_failure),
                    color = MaterialTheme.colorScheme.error
                )

                null -> Spacer(modifier = Modifier.height(0.dp))
            }

            if (state.isReadyForNext && !state.isGameOver) {
                Button(
                    onClick = { viewModel.onNextChallenge() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.next_challenge))
                }
            }

            Keypad(
                feedback = state.feedback,
                inputValue = state.inputValue,
                onDigitPressed = { digit -> viewModel.onDigitPressed(digit) },
                onDelete = { viewModel.onDelete() },
                onSubmit = { viewModel.submitAnswer() },
                isCompact = false
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (state.isGameOver) {
        GameOverDialog(onDismiss = {
            viewModel.onQuitGame()
            onBack()
        })
    }
}
