package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.preview

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard.Keypad
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.HeaderInfo
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.WriteChallengeCard
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.state.MathWriteUiState

@Preview(showBackground = true)
@Composable
private fun MathWriteGameScreenPreview() {
    // Create a sample challenge
    val sampleChallenge = MathWriteChallenge(
        firstOperand = 5,
        secondOperand = 3,
        operation = '+',
        answer = 8
    )

    // Create a sample UI state
    val sampleState = MathWriteUiState(
        challenge = sampleChallenge,
        inputValue = "5",
        score = 125,
        highScore = 250,
        level = 2,
        lives = 2,
        timeRemaining = 15_000L,
        totalTime = 20_000L,
        feedback = null,
        isGameOver = false
    )

    MathBrainerTheme {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.Companion.padding(4.dp))
                    Column {
                        Text(
                            text = "Math Write Game",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Preview Mode",
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
                // Header with game info
                HeaderInfo(state = sampleState)

                // Challenge card with the math problem
                WriteChallengeCard(state = sampleState)

                // Keypad for input
                Keypad(
                    feedback = sampleState.feedback,
                    inputValue = sampleState.inputValue,
                    onDigitPressed = { },
                    onDelete = { },
                    onSubmit = { }
                )
            }
        }
    }
}
