package eu.indiewalkabout.mathbrainer.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily

val Typography = Typography(
    bodyLarge = Typography().bodyLarge.copy(fontFamily = FontFamily.SansSerif),
    titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily.SansSerif),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = FontFamily.SansSerif)
)