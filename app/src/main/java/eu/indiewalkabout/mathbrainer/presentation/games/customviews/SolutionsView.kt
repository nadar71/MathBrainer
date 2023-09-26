package eu.indiewalkabout.mathbrainer.presentation.games.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import java.util.ArrayList

import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.games.customviews.model.CircularImage
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility

class SolutionsView : View {

    internal lateinit var context: Context

    // scaling items images
    internal var imageScaleXY: Float = 0.toFloat()


    // list of marker with number upon
    internal lateinit var localSolutionsList: MutableList<CircularImage>


    private var mWidth: Float = 0.toFloat()                    // Custom view width
    private var mHeight: Float = 0.toFloat()                   // Custom view height


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context,
                attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Rest the view state
     * ---------------------------------------------------------------------------------------------
     */
    private fun init(context: Context) {
        this.context = context

        imageScaleXY = 0.2f

        localSolutionsList = ArrayList()

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Start over new game
     * ---------------------------------------------------------------------------------------------
     */
    fun resetGame() {
        // clear from previous items
        localSolutionsList.clear()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set solution list from MarkerWithNoNumber to localSolutionsList
     * ---------------------------------------------------------------------------------------------
     */
    fun setSolutionList(localSolutionsList: MutableList<CircularImage>) {
        this.localSolutionsList = localSolutionsList
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // resetGame();

        //A paint object that does our drawing, on our canvas
        val paint = Paint()

        //Set the background color
        canvas.drawColor(Color.TRANSPARENT)

        //Change the color of the virtual paint brush
        paint.color = Color.argb(255, 1, 255, 255)

        // draw solutions images currently discovered taken from MarkerWithNoNumber
        if (localSolutionsList.size > 0) {
            for (i in localSolutionsList.indices) {

                val solutionImg = localSolutionsList[i]

                // draw new marker with corrispondent number on it
                val res = resources
                val marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer_green)

                // make bitmap mutable for marker with number
                val bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true)

                // draw text on bitmap
                MathBrainerUtility.drawTextToBitmap(context, bitmapWithNumber,
                        Integer.toString(solutionImg._number))


                // draw on canvas marker with  number on them
                canvas.drawBitmap(
                    MathBrainerUtility.resizeBitmapByScale(bitmapWithNumber, imageScaleXY),
                        solutionImg._x.toFloat(), solutionImg._y.toFloat(), paint)
            }
        }

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Called during layout when the size of this view has changed. If
     * the view was just added to the view hierarchy, it is called with the initial
     * values of 0. The code determines the drawing bounds for the custom view.
     *
     * @param w    Current width of this view
     * @param h    Current height of this view
     * @param oldw Initial width of this view
     * @param oldh Initial height of this view
     * ---------------------------------------------------------------------------------------------
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Calculate the radius from the width and height.
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw view on screen with a defined items number
     * ---------------------------------------------------------------------------------------------
     */
    fun redraw() {
        this.invalidate()
    }

    companion object {
        private val TAG = SolutionsView::class.java.simpleName
    }
}
