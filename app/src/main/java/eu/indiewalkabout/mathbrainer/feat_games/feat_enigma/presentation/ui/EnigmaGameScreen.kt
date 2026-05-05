package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui

// Enigma presents equation-style logic puzzles that the player solves by finding the missing value.

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
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard.Keypad
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.components.EnigmaChallengeCard
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.components.EnigmaHeader
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes

@Composable
fun EnigmaGameScreen(
    initialHighScore: Int = 0,
    onBack: () -> Unit,
    viewModel: EnigmaViewModel = hiltViewModel(),
) {
    val gameId = GameTypes.ENIGMA.id

    LaunchedEffect(gameId, initialHighScore) {
        viewModel.initialize(gameId, initialHighScore)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                    viewModel.onBackPressed()
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
                        text = stringResource(id = R.string.enigma_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(id = R.string.enigma_subtitle),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EnigmaHeader(state)
            state.challenge?.let { challenge ->
                EnigmaChallengeCard(
                    challenge = challenge,
                    inputValue = state.inputValue
                )
            }

            when (state.feedback) {
                ChallengeUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.ok_str),
                    color = MaterialTheme.colorScheme.primary
                )
                ChallengeUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.wrong_answer),
                    color = MaterialTheme.colorScheme.error
                )
                null -> ResultBanner(
                    text = stringResource(id = R.string.enigma_prompt),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

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
