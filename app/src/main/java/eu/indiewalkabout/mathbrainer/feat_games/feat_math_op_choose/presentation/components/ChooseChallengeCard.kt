package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.MathChooseChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.state.MathChooseUiState

@Composable
fun ChooseChallengeCard(state: MathChooseUiState, onOptionSelected: (Int) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Text(
                    text = state.challenge?.firstOperand?.toString().orEmpty(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.Companion.padding(horizontal = 8.dp))
                Text(
                    text = state.challenge?.operation?.toString().orEmpty(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.Companion.padding(horizontal = 8.dp))
                Text(
                    text = state.challenge?.secondOperand?.toString().orEmpty(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.Companion.padding(horizontal = 8.dp))
                Text(
                    text = "= ?",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                text = stringResource(id = R.string.choose_op_instructions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            FlowRow(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.challenge?.options.orEmpty().forEach { option ->
                    Button(onClick = { onOptionSelected(option) }) {
                        Text(
                            text = option.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            when (state.feedback) {
                MathChooseUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.ok_str),
                    color = MaterialTheme.colorScheme.primary
                )

                MathChooseUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.wrong_answer),
                    color = MaterialTheme.colorScheme.error
                )

                null -> Unit
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseChallengeCardPreview() {
    ChooseChallengeCard(
        state = MathChooseUiState(
            challenge = MathChooseChallenge(5, 3, '+', 8, listOf(7, 8, 9)),
            feedback = MathChooseUiState.Feedback.SUCCESS
        ),
        onOptionSelected = {}
    )
}