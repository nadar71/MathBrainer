package eu.indiewalkabout.mathbrainer.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ResultBanner(text: String, color: Color) {
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(color.copy(alpha = 0.15f))
            .padding(12.dp),
        contentAlignment = Alignment.Companion.Center
    ) {
        Text(text = text, color = color, style = MaterialTheme.typography.bodyLarge)
    }
}