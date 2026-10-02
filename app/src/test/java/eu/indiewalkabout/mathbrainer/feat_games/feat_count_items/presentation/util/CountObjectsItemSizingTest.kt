package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CountObjectsItemSizingTest {

    @Test
    fun `phone canvas preserves the existing twenty percent scale`() {
        val scale = calculateCountObjectScale(
            imageWidth = 573,
            imageHeight = 573,
            canvasWidth = 976,
            canvasHeight = 708
        )

        assertEquals(0.2f, scale, 0.001f)
    }

    @Test
    fun `seven inch canvas limits items using available width`() {
        val scale = calculateCountObjectScale(
            imageWidth = 573,
            imageHeight = 573,
            canvasWidth = 544,
            canvasHeight = 236
        )

        assertEquals((544f / 7f) / 573f, scale, 0.001f)
    }

    @Test
    fun `ten inch canvas limits items using available height`() {
        val scale = calculateCountObjectScale(
            imageWidth = 573,
            imageHeight = 573,
            canvasWidth = 744,
            canvasHeight = 236
        )

        assertEquals((236f / 3f) / 573f, scale, 0.001f)
    }

    @Test
    fun `small canvas still produces a positive scale`() {
        val scale = calculateCountObjectScale(
            imageWidth = 573,
            imageHeight = 573,
            canvasWidth = 140,
            canvasHeight = 90
        )

        assertTrue(scale > 0f)
        assertEquals((140f / 7f) / 573f, scale, 0.001f)
    }
}
