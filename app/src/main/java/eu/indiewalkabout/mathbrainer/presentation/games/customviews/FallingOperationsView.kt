package eu.indiewalkabout.mathbrainer.presentation.games.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.presentation.games.othergames.model.FallingOperation

class FallingOperationsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.background_gray_03)
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(2f)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        textSize = dpToPx(18f)
        textAlign = Paint.Align.CENTER
    }

    private val rectRadius = dpToPx(10f)
    private val rectWidth = dpToPx(120f)
    private val rectHeight = dpToPx(56f)

    private var operations: List<FallingOperation> = emptyList()

    fun updateOperations(newOperations: List<FallingOperation>) {
        operations = newOperations
        invalidate()
    }

    fun operationWidth(): Float = rectWidth

    fun operationHeight(): Float = rectHeight

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        operations.forEach { operation ->
            val rect = RectF(
                operation.x,
                operation.y,
                operation.x + rectWidth,
                operation.y + rectHeight
            )
            canvas.drawRoundRect(rect, rectRadius, rectRadius, rectPaint)
            canvas.drawRoundRect(rect, rectRadius, rectRadius, strokePaint)
            val textY = operation.y + rectHeight / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(operation.expression, operation.x + rectWidth / 2f, textY, textPaint)
        }
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }
}
