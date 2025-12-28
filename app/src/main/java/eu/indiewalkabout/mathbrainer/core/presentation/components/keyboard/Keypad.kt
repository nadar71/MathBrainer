package eu.indiewalkabout.mathbrainer.core.presentation.components.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(if (isCompact) 2.dp else 4.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 2.dp else 4.dp),
                horizontalArrangement = Arrangement.spacedBy(if (isCompact) 2.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items((1..9).toList()) { digit ->
                    KeypadButton(
                        text = digit.toString(),
                        isCompact = isCompact
                    ) { onDigitPressed(digit) }
                }
                item { 
                    KeypadButton(
                        text = "0",
                        isCompact = isCompact
                    ) { onDigitPressed(0) } 
                }
                item {
                    KeypadButton(
                        icon = Icons.Default.Backspace,
                        contentDescription = R.string.delete_label,
                        isCompact = isCompact
                    ) {
                        onDelete()
                    }
                }
                item {
                    KeypadButton(
                        text = stringResource(id = R.string.submit_label),
                        highlight = true,
                        isCompact = isCompact
                    ) {
                        if (feedback == null) onSubmit()
                    }
                }
            }
            /*Spacer(modifier = Modifier.Companion.height(8.dp))
            Text(
                text = stringResource(id = R.string.current_input, inputValue.ifEmpty { "-" }),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )*/
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
