package eu.indiewalkabout.mathbrainer.core.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Yellow01,
    onPrimary = TextColor,
    secondary = ButtonBackground01,
    onSecondary = ColorAccent,
    secondaryContainer = BackgroundGray01,
    onSecondaryContainer = ColorAccent,
    background = BackgroundGray02,
    onBackground = ColorAccent,
    surface = BackgroundGray03,
    onSurface = ColorAccent,
    surfaceVariant = TransparentGray,
    onSurfaceVariant = ColorAccent
)

@Composable
fun MathBrainerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}
