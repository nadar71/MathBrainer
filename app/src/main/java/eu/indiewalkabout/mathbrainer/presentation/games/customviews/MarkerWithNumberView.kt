package eu.indiewalkabout.mathbrainer.presentation.games.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.model.CircularImage
import java.util.ArrayList

class MarkerWithNumberView : View {

    internal lateinit var context: Context

    // scaling items images
    internal var imageScaleXY: Float = 0.toFloat()

    // item coords
    internal var randX: Int = 0
    internal var randY: Int = 0

    // list of marker with number upon
    internal lateinit var imgNumberList: MutableList<CircularImage>

    private var mWidth: Float = 0.toFloat() // Custom view width
    private var mHeight: Float = 0.toFloat() // Custom view height

    // number of items to be drawn, modificed in redrawn function
    private var itemNumber = 5

    /**
     * ---------------------------------------------------------------------------------------------
     * Take the imgNumberList to pass to MarkerWithNoNumberView
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val imgwithNUmberList: List<CircularImage>
        get() = imgNumberList

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        this.context = context

        imageScaleXY = 0.2f

        imgNumberList = ArrayList()

        randX = (mWidth * 0.5).toInt()
        randY = (mHeight * 0.5).toInt()

        Log.d(TAG, "onCreate: mWidth : $mWidth mHeigth : $mHeight")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // A paint object that does our drawing, on our canvas
        val paint = Paint()

        // Set the background color
        canvas.drawColor(Color.TRANSPARENT)

        // Change the color of the virtual paint brush
        paint.color = Color.argb(255, 1, 255, 255)

        // clear from previous items
        imgNumberList.clear()

        // draw itemNumber images to count for
        for (i in 0 until itemNumber) {

            val res = resources
            val marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer)

            // get the img size (it's square) with scale
            val size = (marker.width.toFloat() * imageScaleXY).toInt()
            val offsetFromBorder = 10 + size

            // make bitmap mutable for marker with number
            val bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true)

            // draw text on bitmap
            MathBrainerUtility.drawTextToBitmap(context, bitmapWithNumber, Integer.toString(i))

            var isOverlap = true

            while (isOverlap) {
                randX = MathBrainerUtility.randRange_ApiCheck(offsetFromBorder, (mWidth - offsetFromBorder).toInt())
                randY = MathBrainerUtility.randRange_ApiCheck(offsetFromBorder, (mHeight - offsetFromBorder).toInt())
                isOverlap = isOverlapping(randX, randY, size)
            }

            // create an image view
            val imgWithNumber = CircularImage(context, randX, randY, size)
            imgWithNumber.setImageBitmap(bitmapWithNumber)
            imgWithNumber._number = i
            imgNumberList.add(imgWithNumber)

            // draw on canvas marker with  number on them
            canvas.drawBitmap(
                MathBrainerUtility.resizeBitmapByScale(bitmapWithNumber, imageScaleXY),
                randX.toFloat(), randY.toFloat(), paint
            )
        }
        Log.d(TAG, "onDraw: ")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Avoid overlapping objects
     * NB : item origin is in left-top vertex
     *
     * @param x
     * @param y
     * @param size
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    fun isOverlapping(x: Int, y: Int, size: Int): Boolean {

        for (circularImage in imgNumberList) {
            if ((x < circularImage._x && x > circularImage._x - size || x > circularImage._x && x < circularImage._x + size) && (y < circularImage._y && y > circularImage._y - size || y > circularImage._y && y < circularImage._y + size)) {
                return true
            }
        }
        return false
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
        Log.d(TAG, "onCreate: mWidth : $mWidth mHeigth : $mHeight")
    }

    // Draw view on screen with a defined items number
    // @param itemNumber
    fun redraw(itemNumber: Int) {
        // this.timerLength = timerLength;
        this.itemNumber = itemNumber
        this.invalidate()
    }
}
