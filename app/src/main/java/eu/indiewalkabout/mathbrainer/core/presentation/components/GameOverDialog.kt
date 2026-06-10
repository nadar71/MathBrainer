package eu.indiewalkabout.mathbrainer.core.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import eu.indiewalkabout.mathbrainer.R



@Composable
fun GameOverDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.game_over_title)) },
        text = { Text(text = stringResource(id = R.string.game_over_body)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.navigate_back))
            }
        }
    )
}


@Preview
@Composable
fun GameOverDialogPreview() {
    GameOverDialog(onDismiss = {})
}
