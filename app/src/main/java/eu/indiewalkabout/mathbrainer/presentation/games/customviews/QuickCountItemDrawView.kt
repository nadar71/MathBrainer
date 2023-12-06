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
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.model.CircularImage
import java.io.IOException
import java.util.ArrayList

// Visualizing  x items distributed all around the view, to guess their number after disappearing
class QuickCountItemDrawView : View {
    internal lateinit var context: Context

    private var mWidth: Float = 0.toFloat() // Custom view width
    private var mHeight: Float = 0.toFloat() // Custom view height

    // number of items to be drawn
    private var itemNumber = 5

    // scaling items images
    internal var imageScaleXY: Float = 0.toFloat()

    // item coords
    internal var randX: Int = 0
    internal var randY: Int = 0

    // list of the items
    internal lateinit var itemList: MutableList<CircularImage>

    // time length before returning
    // int timerLength = 0;

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
        itemList = ArrayList()
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
        itemList.clear()

        // draw itemNumber images to count for
        for (i in 0 until itemNumber) {

            try {
                // get images
                val assetManager = context.assets
                val inputStream = assetManager.open("memo" + (100 + i) + ".png")
                var item = BitmapFactory.decodeStream(inputStream)

                // get the img size (it's square) with scale
                val size = (item.width.toFloat() * imageScaleXY).toInt()
                val offsetFromBorder = 10 + size

                inputStream.close()

                // make bitmap mutable
                item = item.copy(Bitmap.Config.ARGB_8888, true)
                var isOverlap = true

                while (isOverlap) {
                    randX = MathBrainerUtility.randRange_ApiCheck(offsetFromBorder, (mWidth - offsetFromBorder).toInt())
                    randY = MathBrainerUtility.randRange_ApiCheck(offsetFromBorder, (mHeight - offsetFromBorder).toInt())
                    isOverlap = isOverlapping(randX, randY, size)
                }

                // add to list
                itemList.add(CircularImage(context, randX, randY, size))

                // draw on canvas
                canvas.drawBitmap(
                    MathBrainerUtility.resizeBitmapByScale(item, imageScaleXY),
                    randX.toFloat(), randY.toFloat(), paint
                )
            } catch (e: IOException) {
                Log.d(TAG, "drawGame: " + e.message)
            } finally {
                // we should really close our input streams here.
            }
        }
    }

    // Avoid overlapping objects
    // NB : item origin is in left-top vertex
    // @param x
    // @param y
    // @param size
    // @return
    fun isOverlapping(x: Int, y: Int, size: Int): Boolean {

        for (circularImage in itemList) {
            if ((x < circularImage._x && x > circularImage._x - size || x > circularImage._x && x < circularImage._x + size) && (y < circularImage._y && y > circularImage._y - size || y > circularImage._y && y < circularImage._y + size)) {
                return true
            }
        }
        return false
    }

    // Called during layout when the size of this view has changed. If
    // the view was just added to the view hierarchy, it is called with the initial
    // values of 0. The code determines the drawing bounds for the custom view.
    //
    // @param w    Current width of this view
    // @param h    Current height of this view
    // @param oldw Initial width of this view
    // @param oldh Initial height of this view
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
