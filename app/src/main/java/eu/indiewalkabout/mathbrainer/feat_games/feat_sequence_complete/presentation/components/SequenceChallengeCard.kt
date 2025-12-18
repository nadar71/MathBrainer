package eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.feat_games.feat_sequence_complete.presentation.state.SequenceCompleteUiState

@Composable
fun SequenceChallengeCard(
    state: SequenceCompleteUiState
) {
    val feedbackColor = when (state.feedback) {
        SequenceCompleteUiState.Feedback.SUCCESS -> MaterialTheme.colorScheme.primary
        SequenceCompleteUiState.Feedback.FAILURE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.visibleSequence.forEachIndexed { index, number ->
                    SequenceNumberChip(
                        text = number?.toString() ?: "?",
                        highlighted = index == state.visibleSequence.lastIndex && state.revealedAnswer != null,
                        textColor = if (state.feedback != null) feedbackColor else null
                    )
                }
            }

            // Input field for the answer
            TextField(
                value = state.inputValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                    // .height(54.dp),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSecondaryContainer,
                    // lineHeight = 40.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Transparent
                ),
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.None
                ),
                placeholder = { 
                    if (state.inputValue.isBlank()) {
                        Text(
                            text = stringResource(id = R.string.sequence_prompt),
                            style = MaterialTheme.typography.labelSmall.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                },
                singleLine = true
            )

            state.ruleDescription?.let { rule ->
                Text(
                    text = stringResource(id = R.string.sequence_rule_reveal, rule),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }


        }
    }
}

@Composable
private fun SequenceNumberChip(
    text: String,
    highlighted: Boolean,
    textColor: Color? = null
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .height(28.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            ),
            color = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center
        )
    }
}
