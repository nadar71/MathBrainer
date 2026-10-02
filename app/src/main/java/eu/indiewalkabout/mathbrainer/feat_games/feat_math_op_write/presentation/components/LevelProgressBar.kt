package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme

@Composable
fun LevelProgressBar(
    currentProgress: Int,
    totalSegments: Int = 10,
    modifier: Modifier = Modifier,
    segmentColor: Color,
    backgroundColor: Color,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        repeat(totalSegments) { index ->
            val segmentWeight = 1f / totalSegments
            val color = if (index < currentProgress) segmentColor else Color.Transparent
            
            Box(
                modifier = Modifier
                    .weight(segmentWeight)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LevelProgressBarPreview() {
    MathBrainerTheme {
        LevelProgressBar(
            currentProgress = 7,
            totalSegments = 10,
            segmentColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}
