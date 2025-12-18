package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components.keyboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme

@Composable
fun KeypadButton(
    text: String? = null,
    icon: ImageVector? = null,
    @StringRes contentDescription: Int? = null,
    highlight: Boolean = false,
    isCompact: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isCompact) 2.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (isCompact) 6.dp else 12.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = contentDescription?.let { stringResource(id = it) },
                    tint = if (highlight) MaterialTheme.colorScheme.onPrimary 
                          else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                )

                text != null -> Text(
                    text = text,
                    style = if (isCompact) MaterialTheme.typography.bodyLarge
                          else MaterialTheme.typography.titleMedium,
                    color = if (highlight) MaterialTheme.colorScheme.onPrimary 
                          else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun KeypadButtonPreview() {
    MathBrainerTheme {
        KeypadButton(
            text = "1",
            highlight = true,
            onClick = {}
        )
    }
}
