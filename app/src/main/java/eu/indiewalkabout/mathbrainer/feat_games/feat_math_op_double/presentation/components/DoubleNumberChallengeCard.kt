package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.components.ResultBanner
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.state.DoubleNumberUiState

@Composable
fun DoubleNumberChallengeCard(state: DoubleNumberUiState) {
    val feedbackColor = when (state.feedback) {
        ChallengeUiState.Feedback.SUCCESS -> MaterialTheme.colorScheme.primary
        ChallengeUiState.Feedback.FAILURE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Text(
                    text = state.challenge?.value?.toString().orEmpty(),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.Companion.padding(horizontal = 8.dp))
                Text(
                    text = "x 2",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.Companion.padding(horizontal = 8.dp))
                Text(
                    text = "=",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.feedback != null) feedbackColor else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            TextField(
                value = state.inputValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.Companion.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Companion.Center),
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
                            text = stringResource(id = R.string.write_result_placeholder),
                            style = MaterialTheme.typography.labelSmall.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                },
                singleLine = true
            )

            when (state.feedback) {
                ChallengeUiState.Feedback.SUCCESS -> ResultBanner(
                    text = stringResource(id = R.string.ok_str),
                    color = MaterialTheme.colorScheme.primary
                )

                ChallengeUiState.Feedback.FAILURE -> ResultBanner(
                    text = stringResource(id = R.string.wrong_answer),
                    color = MaterialTheme.colorScheme.error
                )

                null -> Unit
            }
        }
    }
}