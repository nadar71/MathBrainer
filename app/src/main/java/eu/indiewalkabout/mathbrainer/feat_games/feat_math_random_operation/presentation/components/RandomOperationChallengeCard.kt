package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.components

import RandomOperationUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.OperationFormatter
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.ExpressionGrouping
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationChallenge

@Composable
fun RandomOperationChallengeCard(
    state: RandomOperationUiState,
    onOperationSelected: (Char) -> Unit
) {
    val challenge = state.challenge ?: return
    val feedbackColor = when (state.feedback) {
        RandomOperationUiState.Feedback.SUCCESS -> MaterialTheme.colorScheme.primary
        RandomOperationUiState.Feedback.FAILURE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.random_operations_instruction),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildExpressionText(
                        challenge = challenge,
                        showAnswer = state.feedback != null
                    ),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (state.feedback != null) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf('+', '-', '×', '/').map { op -> op to (if (op == '×') '*' else op) }.forEach { (displayOp, internalOp) ->
                    Button(
                        onClick = { onOperationSelected(internalOp) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = displayOp.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun buildExpressionText(
    challenge: RandomOperationChallenge,
    showAnswer: Boolean
): String {
    val operands = challenge.operands
    val operations = challenge.operations
    val hiddenIndex = challenge.hiddenOperationIndex
    val displayedOperations = operations.mapIndexed { index, op ->
        if (index == hiddenIndex) {
            if (showAnswer) OperationFormatter.format(op) else "?"
        } else {
            OperationFormatter.format(op)
        }
    }

    val expression = when (challenge.grouping) {
        ExpressionGrouping.NONE -> {
            "${operands[0]} ${displayedOperations[0]} ${operands[1]}"
        }

        ExpressionGrouping.LEFT -> {
            "(${operands[0]} ${displayedOperations[0]} ${operands[1]}) ${displayedOperations[1]} ${operands[2]}"
        }

        ExpressionGrouping.RIGHT -> {
            "${operands[0]} ${displayedOperations[0]} (${operands[1]} ${displayedOperations[1]} ${operands[2]})"
        }
    }

    return "$expression = ${challenge.result}"
}
