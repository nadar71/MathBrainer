package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.util

import kotlin.math.max

internal fun calculateCountObjectScale(
    imageWidth: Int,
    imageHeight: Int,
    canvasWidth: Int,
    canvasHeight: Int
): Float {
    val imageMaxSide = max(imageWidth, imageHeight).coerceAtLeast(1)
    val targetMaxSide = minOf(
        imageMaxSide * BASE_IMAGE_SCALE,
        canvasWidth.coerceAtLeast(1) / CANVAS_WIDTH_DIVISOR,
        canvasHeight.coerceAtLeast(1) / CANVAS_HEIGHT_DIVISOR
    ).coerceAtLeast(1f)

    return targetMaxSide / imageMaxSide
}

private const val BASE_IMAGE_SCALE = 0.2f
private const val CANVAS_WIDTH_DIVISOR = 7f
private const val CANVAS_HEIGHT_DIVISOR = 3f
