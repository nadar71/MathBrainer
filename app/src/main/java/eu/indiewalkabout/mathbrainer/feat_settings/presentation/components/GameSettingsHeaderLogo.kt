package eu.indiewalkabout.mathbrainer.feat_settings.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.R

@Composable
fun GameSettingsHeaderLogo() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.Companion.fillMaxWidth()) {
            Canvas(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val gradient = Brush.Companion.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.75f),
                        secondaryColor.copy(alpha = 0.75f)
                    )
                )

                withTransform({
                    rotate(
                        degrees = -12f,
                        pivot = Offset(size.width / 2f, size.height / 2f)
                    )
                }) {
                    drawRect(
                        brush = gradient,
                        topLeft = Offset(-size.width * 0.1f, size.height * 0.05f),
                        size = Size(size.width * 1.2f, size.height * 0.7f)
                    )
                }

                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = size.minDimension / 4f,
                    center = Offset(size.width * 0.85f, size.height * 0.2f)
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.18f),
                    radius = size.minDimension / 3.5f,
                    center = Offset(size.width * 0.15f, size.height * 0.8f)
                )
            }

            Column(
                modifier = Modifier.Companion
                    .align(Alignment.Companion.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.credits_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = onSurfaceVariant,
                    fontWeight = FontWeight.Companion.Bold,
                    textAlign = TextAlign.Companion.Center
                )
                Spacer(modifier = Modifier.Companion.height(8.dp))
                Text(
                    text = stringResource(id = R.string.credits_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = onSurfaceVariant,
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
    }
}