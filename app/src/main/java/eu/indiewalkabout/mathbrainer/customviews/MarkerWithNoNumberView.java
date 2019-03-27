package eu.indiewalkabout.mathbrainer.customviews;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.model.CircularImage;
import eu.indiewalkabout.mathbrainer.othergames.NumberOrderActivity;
import eu.indiewalkabout.mathbrainer.util.myUtil;

public class MarkerWithNoNumberView extends View {
    private static final String TAG = MarkerWithNoNumberView.class.getSimpleName();

    Context context;

    // scaling items images
    float imageScaleXY;

    // item coords
    int randX;
    int randY;


    SolutionsView show_solutions;


    // list of marker with number upon
    List<CircularImage> imgNumberList;

    // list of marker with no number upon
    List<CircularImage> imgNoNumberList;

    // list of marker with solution upon, to be passed to SolutionsView
    List<CircularImage> solutionsList;



    // touch result :
    // -1 : no touch
    // 0  : wrong touch
    // 1  : touch ok
    private MutableLiveData<Integer> touchResult = new MutableLiveData<>();


    private float mWidth;                    // Custom view width
    private float mHeight;                   // Custom view height

    // number of items to be drawn
    private int itemNumber = 5;

    // number of correct touch counter
    private int touchCounter = 0;

    // touch marker enable/disable
    private boolean isTouchMarkersEnable = true;


    public MarkerWithNoNumberView(Context context) {
        super(context);
        init(context);
    }

    public MarkerWithNoNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarkerWithNoNumberView(Context context,
                                AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    // Setters/ getters for touch enabling flag
    public void set_isTouchMarkersEnable(boolean state){
        isTouchMarkersEnable = state;
    }

    public boolean get_isTouchMarkersEnable(){
        return isTouchMarkersEnable;
    }


    public List<CircularImage> get_solutionList(){
        return solutionsList;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Init view, used in constructors
     * @param context
     * ---------------------------------------------------------------------------------------------
     */
    private void init(Context context) {
        this.context = context;

        touchCounter = 0;

        imageScaleXY = 0.2f;

        imgNumberList   = new ArrayList<>();
        imgNoNumberList = new ArrayList<>();
        solutionsList   = new ArrayList<>();

        randX = (int) (mWidth * 0.5);
        randY = (int) (mHeight * 0.5);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Rest the view state
     * ---------------------------------------------------------------------------------------------
     */
    public void resetGame(){
        // clear list from previous items
        imgNoNumberList.clear();

        // clear list of discovered solutions
        solutionsList.clear();

        // reset touch counter
        touchCounter = 0;

        // init touch result flag to neuter value, to avoid triggering action
        touchResult.setValue(-1);

        // enable view to touch
        set_isTouchMarkersEnable(true);

        // reset solutions data structure
        show_solutions.resetGame();
    }


    /**
     * ----------------------------------------------------------------------------------------------
     * Touch result setter
     * @param value
     * ---------------------------------------------------------------------------------------------
     */
    public void setTouchResult(int value){
        touchResult.setValue(value);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Touch result getter
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public MutableLiveData<Integer> getTouchResult(){
        return touchResult;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Used to get the list of image from MarkerWithNUmber, to set same positions
     * @param imgNumberList
     * ---------------------------------------------------------------------------------------------
     */
    public void setImgNumberList(List<CircularImage> imgNumberList){
        this.imgNumberList = imgNumberList;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Get the SolutionsView instance
     * @param show_solutions
     * ---------------------------------------------------------------------------------------------
     */
    public void setSolutionView(SolutionsView show_solutions){
        this.show_solutions = show_solutions;

    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // start a game as new
        resetGame();

        //A paint object that does our drawing, on our canvas
        Paint paint = new Paint();

        //Set the background color
        canvas.drawColor(Color.TRANSPARENT);

        //Change the color of the virtual paint brush
        paint.setColor(Color.argb(255, 1, 255, 255));


        // draw itemNumber images to count for
        for (CircularImage img : imgNumberList) {

            Resources res = getResources();
            Bitmap marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer);

            // get the img size (it's square) with scale
            int size = ((int) ((float) marker.getWidth() * imageScaleXY));

            // create bitmap with no number upon it, and add to list
            int x = img.get_x();
            int y = img.get_y();
            Bitmap bitmapWithNoNumber = marker.copy(Bitmap.Config.ARGB_8888, true);
            CircularImage imgWithNoNumber = new CircularImage(context, x , y , size);
            imgWithNoNumber.set_bitmap(bitmapWithNoNumber);
            imgNoNumberList.add(imgWithNoNumber);

            // draw on canvas marker with no number on them
            canvas.drawBitmap(myUtil.resizeBitmapByScale(bitmapWithNoNumber, imageScaleXY),
                    x, y, paint);
        }



    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Support positional touch event, i.e. : if touch is inside a bitmap, trigger action
     * @param event
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (! isTouchMarkersEnable) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                for (CircularImage img : imgNoNumberList) {
                    //Check if the x and y position of the touch is inside the bitmap
                    if (x > img.get_x() && x < img.get_x() + img.getSize() &&
                        y > img.get_y() && y < img.get_y() + img.getSize() &&
                       (! img.get_touched() )){

                        int indx = imgNoNumberList.indexOf(img);
                        img.set_touched(true); // marked image as touched

                        if ( indx == touchCounter) {

                            // mark as a correct result
                            loadSolution(img, indx);
                            show_solutions.redraw();

                            // win condition reached
                            if ( allMarkerTouched() && (touchCounter == itemNumber-1) ) {
                                touchResult.setValue(1);
                            }
                            touchCounter++;

                        }else {
                            // marked image as NOT touched: unnecessary, but make win condition check more robust
                            img.set_touched(false);
                            touchResult.setValue(0);
                        }
                    }
                }
                return true;
        }
        return false;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Load the list  solutionsListof SolutionsView to keep update for showing correct solutions
     * once they are discovered
     * ---------------------------------------------------------------------------------------------
     */
    private void loadSolution(CircularImage img, int number){

        Bitmap marker = img.get_bitmap();

        // make bitmap mutable for marker with number
        Bitmap bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true);

        // draw text on bitmap
        myUtil.drawTextToBitmap(context, bitmapWithNumber, Integer.toString(number));

        // create an image view and store in solutions list
        CircularImage solutionsImg = new CircularImage(context, img.get_x(), img.get_y(), img.getSize());
        solutionsImg.setImageBitmap(bitmapWithNumber);
        solutionsImg.set_number(number);
        solutionsList.add(solutionsImg);

        // make SolutionsView aware of solutions list images
        show_solutions.setSolutionList(solutionsList);

    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Check if all marker are touched.
     * Win condition because if a wrong the marker is touched,
     * is not set as touched
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private boolean allMarkerTouched(){
        for(CircularImage img : imgNoNumberList){
            if (img.get_touched() == false) return false;
        }
        return true;
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
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth  = w;
        mHeight = h;
        Log.d(TAG, "onCreate: mWidth : " + mWidth + " mHeigth : " + mHeight);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw view on screen with a defined items number
     *
     * @param itemNumber
     * ---------------------------------------------------------------------------------------------
     */
    public void redraw(int itemNumber) {
        // this.timerLength = timerLength;
        this.itemNumber = itemNumber;
        this.invalidate();
    }

}

