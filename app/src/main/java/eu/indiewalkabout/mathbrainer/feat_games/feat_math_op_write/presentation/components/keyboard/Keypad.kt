package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.keyboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme

@Composable
fun Keypad(
    inputValue: String,
    onDigitPressed: (Int) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp)
            ) {
                items((1..9).toList()) { digit ->
                    KeypadButton(text = digit.toString()) { onDigitPressed(digit) }
                }
                item { KeypadButton(text = "0") { onDigitPressed(0) } }
                item {
                    KeypadButton(
                        icon = Icons.Default.Backspace,
                        contentDescription = R.string.delete_label
                    ) {
                        onDelete()
                    }
                }
                item {
                    KeypadButton(
                        text = stringResource(id = R.string.submit_label),
                        highlight = true
                    ) {
                        onSubmit()
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
            inputValue = "123",
            onDigitPressed = {},
            onDelete = {},
            onSubmit = {}
        )
    }
}
