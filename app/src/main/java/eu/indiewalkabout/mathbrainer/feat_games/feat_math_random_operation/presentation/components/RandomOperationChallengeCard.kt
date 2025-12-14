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
                // First operand - always visible
                Text(
                    text = state.challenge.firstOperand.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (state.feedback != null) FontWeight.Bold else FontWeight.Normal
                )
                
                // Operation symbol - only shown after feedback
                if (state.feedback != null) {
                    Text(
                        text = " ${OperationFormatter.format(challenge.correctOperation)} ",
                        style = MaterialTheme.typography.displaySmall,
                        color = feedbackColor,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    // Show question mark before answer
                    Text(
                        text = " ? ",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Second operand - always visible
                Text(
                    text = state.challenge.secondOperand.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (state.feedback != null) FontWeight.Bold else FontWeight.Normal
                )
                
                // Equals and result - always visible
                Text(
                    text = " = ",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = challenge.result.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
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