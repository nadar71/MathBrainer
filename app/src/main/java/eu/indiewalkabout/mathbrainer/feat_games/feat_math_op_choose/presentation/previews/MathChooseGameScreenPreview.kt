package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.previews

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.MathChooseChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.components.ChooseChallengeCard
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.components.ChooseHeaderInfo
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.state.MathChooseUiState

@Composable
fun StaticMathChooseGameScreen(
    state: MathChooseUiState = previewMathChooseState,
    onBack: () -> Unit = {},
    onOptionSelected: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.navigate_back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.math_choose_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(id = R.string.math_choose_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
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
            ChooseHeaderInfo(
                state = state,
                levelChallengesCompleted = state.challengesCompleted,
                levelChallengesTarget = state.challengesPerLevel
            )
            ChooseChallengeCard(
                state = state,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

// Preview state
private val previewMathChooseState = MathChooseUiState(
    challenge = MathChooseChallenge(
        firstOperand = 5,
        secondOperand = 3,
        operation = '+',
        correctAnswer = 8,
        options = listOf(7, 8, 9, 10)
    ),
    score = 125,
    highScore = 250,
    level = 2,
    challengesCompleted = 3,
    challengesPerLevel = 5,
    lives = 3,
    timeRemaining = 15000L,
    totalTime = 20000L,
    feedback = null,
    isGameOver = false
)

@Preview(showBackground = true)
@Composable
fun MathChooseGameScreenPreview() {
    MathBrainerTheme {
        StaticMathChooseGameScreen(
            state = previewMathChooseState,
            onBack = {},
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Game Over Preview")
@Composable
fun MathChooseGameScreenGameOverPreview() {
    MathBrainerTheme {
        StaticMathChooseGameScreen(
            state = previewMathChooseState.copy(
                isGameOver = true,
                lives = 0
            ),
            onBack = {},
            onOptionSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Last Life Preview")
@Composable
fun MathChooseGameScreenLastLifePreview() {
    MathBrainerTheme {
        StaticMathChooseGameScreen(
            state = previewMathChooseState.copy(
                lives = 1,
                feedback = ChallengeUiState.Feedback.FAILURE
            ),
            onBack = {},
            onOptionSelected = {}
        )
    }
}
