package eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme

@Composable
fun Keypad(
    feedback: ChallengeUiState.Feedback?,
    inputValue: String,
    onDigitPressed: (Int) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    isCompact: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isCompact) 4.dp else 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 4.dp else 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 2.dp else 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(if (isCompact) 2.dp else 4.dp))
            ) {
                (1..9).chunked(3).forEach { rowDigits ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 2.dp else 4.dp)
                    ) {
                        rowDigits.forEach { digit ->
                            KeypadButton(
                                modifier = Modifier.weight(1f),
                                text = digit.toString(),
                                isCompact = isCompact
                            ) { onDigitPressed(digit) }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (isCompact) 2.dp else 4.dp)
                ) {
                    KeypadButton(
                        modifier = Modifier.weight(1f),
                        text = "0",
                        isCompact = isCompact
                    ) { onDigitPressed(0) }
                    KeypadButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Backspace,
                        contentDescription = R.string.delete_label,
                        isCompact = isCompact
                    ) {
                        onDelete()
                    }
                    KeypadButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.submit_label),
                        highlight = true,
                        isCompact = isCompact
                    ) {
                        if (feedback == null) onSubmit()
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun KeypadPreview() {
    MathBrainerTheme{
        Keypad(
            feedback = null,
            inputValue = "123",
            onDigitPressed = {},
            onDelete = {},
            onSubmit = {}
        )
    }
}
