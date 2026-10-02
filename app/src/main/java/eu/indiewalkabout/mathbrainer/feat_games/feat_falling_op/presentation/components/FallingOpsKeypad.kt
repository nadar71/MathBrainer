package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FallingOpsKeypad(
    feedback: ChallengeUiState.Feedback?,
    inputValue: String,
    onDigitPressed: (Int) -> Unit,
    onDelete: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // First row: 1, 2, 3, 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(1, 2, 3, 4).forEach { digit ->
                FallingOpsKeyButton(
                    text = digit.toString(),
                    modifier = Modifier.weight(1f),
                    onClick = { onDigitPressed(digit) }
                )
            }
        }

        // Second row: 5, 6, 7, 8
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(5, 6, 7, 8).forEach { digit ->
                FallingOpsKeyButton(
                    text = digit.toString(),
                    modifier = Modifier.weight(1f),
                    onClick = { onDigitPressed(digit) }
                )
            }
        }

        // Third row:  9
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            /*listOf(7, 8, 9).forEach { digit ->
                FallingOpsKeyButton(
                    text = digit.toString(),
                    modifier = Modifier.weight(1f),
                    onClick = { onDigitPressed(digit) }
                )
            }*/
            // 9
            FallingOpsKeyButton(
                text = "9",
                modifier = Modifier.weight(1f),
                onClick = { onDigitPressed(9) }
            )

            // 0
            FallingOpsKeyButton(
                text = "0",
                modifier = Modifier.weight(1f),
                onClick = { onDigitPressed(0) }
            )

            // Backspace
            FallingOpsKeyButton(
                modifier = Modifier.weight(1f),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.delete_label),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Enter/Submit
            FallingOpsKeyButton(
                text = stringResource(id = R.string.submit_label),
                modifier = Modifier.weight(1f),
                highlight = true,
                onClick = {onSubmit()}//{ if (feedback == null) onSubmit() }
            )
        }

    }
}

@Composable
private fun FallingOpsKeyButton(
    text: String? = null,
    highlight: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val backgroundColor = if (highlight) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (highlight) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            //.padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        } else {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FallingOpsKeypadPreview() {
    MathBrainerTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            FallingOpsKeypad(
                feedback = null,
                inputValue = "123",
                onDigitPressed = {},
                onDelete = {},
                onSubmit = {}
            )
        }
    }
}
