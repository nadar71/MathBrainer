package eu.indiewalkabout.mathbrainer.presentation.games.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.model.CircularImage
import java.util.ArrayList

class MarkerWithNoNumberView : View {

    internal lateinit var context: Context

    // scaling items images
    internal var imageScaleXY: Float = 0.toFloat()

    // item coords
    internal var randX: Int = 0
    internal var randY: Int = 0

    internal lateinit var show_solutions: SolutionsView

    // list of marker with number upon
    internal lateinit var imgNumberList: List<CircularImage>

    // list of marker with no number upon
    internal lateinit var imgNoNumberList: MutableList<CircularImage>

    // list of marker with solution upon, to be passed to SolutionsView
    internal lateinit var solutionsList: MutableList<CircularImage>

    // touch result :
    // -1 : no touch
    // 0  : wrong touch
    // 1  : touch ok
    /**
     * ---------------------------------------------------------------------------------------------
     * Touch result getter
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val touchResult = MutableLiveData<Int>()

    private var mWidth: Float = 0.toFloat() // Custom view width
    private var mHeight: Float = 0.toFloat() // Custom view height

    // number of items to be drawn
    private var itemNumber = 5

    // number of correct touch counter
    private var touchCounter = 0

    // touch marker enable/disable
    // Setters/ getters for touch enabling flag
    var _isTouchMarkersEnable = true

    val _solutionList: List<CircularImage>
        get() = solutionsList

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

    /**
     * ---------------------------------------------------------------------------------------------
     * Init view, used in constructors
     * @param context
     * ---------------------------------------------------------------------------------------------
     */
    private fun init(context: Context) {
        this.context = context

        touchCounter = 0

        imageScaleXY = 0.2f

        imgNumberList = ArrayList()
        imgNoNumberList = ArrayList()
        solutionsList = ArrayList()

        randX = (mWidth * 0.5).toInt()
        randY = (mHeight * 0.5).toInt()
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Rest the view state
     * ---------------------------------------------------------------------------------------------
     */
    fun resetGame() {
        // clear list from previous items
        imgNoNumberList.clear()

        // clear list of discovered solutions
        solutionsList.clear()

        // reset touch counter
        touchCounter = 0

        // init touch result flag to neuter value, to avoid triggering action
        touchResult.value = -1

        // enable view to touch
        _isTouchMarkersEnable = true

        // reset solutions data structure
        show_solutions.resetGame()
    }

    /**
     * ----------------------------------------------------------------------------------------------
     * Touch result setter
     * @param value
     * ---------------------------------------------------------------------------------------------
     */
    fun setTouchResult(value: Int) {
        touchResult.value = value
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Used to get the list of image from MarkerWithNUmber, to set same positions
     * @param imgNumberList
     * ---------------------------------------------------------------------------------------------
     */
    fun setImgNumberList(imgNumberList: List<CircularImage>) {
        this.imgNumberList = imgNumberList
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Get the SolutionsView instance
     * @param show_solutions
     * ---------------------------------------------------------------------------------------------
     */
    fun setSolutionView(show_solutions: SolutionsView) {
        this.show_solutions = show_solutions
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // start a game as new
        resetGame()

        // A paint object that does our drawing, on our canvas
        val paint = Paint()

        // Set the background color
        canvas.drawColor(Color.TRANSPARENT)

        // Change the color of the virtual paint brush
        paint.color = Color.argb(255, 1, 255, 255)

        // draw itemNumber images to count for
        for (img in imgNumberList) {

            val res = resources
            val marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer)

            // get the img size (it's square) with scale
            val size = (marker.width.toFloat() * imageScaleXY).toInt()

            // create bitmap with no number upon it, and add to list
            val x = img._x
            val y = img._y
            val bitmapWithNoNumber = marker.copy(Bitmap.Config.ARGB_8888, true)
            val imgWithNoNumber = CircularImage(context, x, y, size)
            imgWithNoNumber._bitmap = bitmapWithNoNumber
            imgNoNumberList.add(imgWithNoNumber)

            // draw on canvas marker with no number on them
            canvas.drawBitmap(
                MathBrainerUtility.resizeBitmapByScale(bitmapWithNoNumber, imageScaleXY),
                x.toFloat(), y.toFloat(), paint
            )
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Support positional touch event, i.e. : if touch is inside a bitmap, trigger action
     * @param event
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!_isTouchMarkersEnable) {
            return false
        }

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (img in imgNoNumberList) {
                    // Check if the x and y position of the touch is inside the bitmap
                    if (x > img._x && x < img._x + img.size &&
                        y > img._y && y < img._y + img.size &&
                        !img._touched
                    ) {

                        val indx = imgNoNumberList.indexOf(img)
                        img._touched = true // marked image as touched

                        if (indx == touchCounter) {

                            // mark as a correct result
                            loadSolution(img, indx)
                            show_solutions.redraw()

                            // win condition reached
                            if (allMarkerTouched() && touchCounter == itemNumber - 1) {
                                sendLevelCompleted() // set level completed correctly with delay
                            }
                            touchCounter++
                        } else {
                            // marked image has NOT touched: unnecessary, but make win condition check more robust
                            img._touched = false
                            touchResult.setValue(0)
                        }
                    }
                }
                return true
            }
        }
        return false
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Send level completed correctly with a bit of delay
     * ---------------------------------------------------------------------------------------------
     */
    private fun sendLevelCompleted() {
        val handler = Handler()
        val runnable = Runnable { touchResult.value = 1 }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, 500)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Load the list  solutionsListof SolutionsView to keep update for showing correct solutions
     * once they are discovered
     * ---------------------------------------------------------------------------------------------
     */
    private fun loadSolution(img: CircularImage, number: Int) {

        val marker = img._bitmap

        // make bitmap mutable for marker with number
        val bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true)

        // draw text on bitmap
        MathBrainerUtility.drawTextToBitmap(context, bitmapWithNumber, Integer.toString(number))

        // create an image view and store in solutions list
        val solutionsImg = CircularImage(context, img._x, img._y, img.size)
        solutionsImg.setImageBitmap(bitmapWithNumber)
        solutionsImg._number = number
        solutionsList.add(solutionsImg)

        // make SolutionsView aware of solutions list images
        show_solutions.setSolutionList(solutionsList)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check if all marker are touched.
     * Win condition because if a wrong the marker is touched,
     * is not set as touched
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private fun allMarkerTouched(): Boolean {
        for (img in imgNoNumberList) {
            if (img._touched == false) return false
        }
        return true
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
