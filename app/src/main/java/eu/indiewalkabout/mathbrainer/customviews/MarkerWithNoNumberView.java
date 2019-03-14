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

    // list of marker with number upon
    List<CircularImage> imgNumberList;

    // list of marker with no number upon
    List<CircularImage> imgNoNumberList;

    // touch result :
    // -1 : no touch
    // 0  : wrong touch
    // 1  : touch ok
    private MutableLiveData<Integer> touchResult = new MutableLiveData<>();


    private float mWidth;                    // Custom view width
    private float mHeight;                   // Custom view height

    // number of items to be drawn
    private int itemNumber = 5;

    private int touchCounter = 0;


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


    private void init(Context context) {
        this.context = context;

        imageScaleXY = 0.2f;

        imgNumberList = new ArrayList<>();
        imgNoNumberList = new ArrayList<>();

        randX = (int) (mWidth * 0.5);
        randY = (int) (mHeight * 0.5);

        Log.d(TAG, "onCreate: mWidth : " + mWidth + " mHeigth : " + mHeight);

    }



    public MutableLiveData<Integer> getTouchResult(){
        return touchResult;
    }


    public void setImgNumberList(List<CircularImage> imgNumberList){
        this.imgNumberList = imgNumberList;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //A paint object that does our drawing, on our canvas
        Paint paint = new Paint();

        //Set the background color
        canvas.drawColor(Color.TRANSPARENT);

        //Change the color of the virtual paint brush
        paint.setColor(Color.argb(255, 1, 255, 255));

        // clear from previous items
        imgNoNumberList.clear();

        // draw itemNumber images to count for
        for (CircularImage img : imgNumberList) {

            // ------------ collect first set of images, with number above ------------
            Resources res = getResources();
            Bitmap marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer);

            // get the img size (it's square) with scale
            int size = ((int) ((float) marker.getWidth() * imageScaleXY));

            // create bitmap with no number upon it, and add to list
            int x = img.get_x();
            int y = img.get_y();
            Bitmap bitmapWithNoNumber = marker.copy(Bitmap.Config.ARGB_8888, true);
            CircularImage imgWithNoNumber = new CircularImage(context, x + 10, y + 10, size);
            imgWithNoNumber.set_bitmap(bitmapWithNoNumber);
            imgNoNumberList.add(imgWithNoNumber);

            // draw on canvas marker with no number on them
            canvas.drawBitmap(myUtil.resizeBitmapByScale(bitmapWithNoNumber, imageScaleXY),
                    x, y, paint);
        }



    }



    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
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

                        // check if the number touched is in the right order: comparing img with number
                        // in imgNumberList with same index
                        /*
                        CircularImage correspondingImgWithNUmber = imgNumberList.get(indx);
                        int indx_imgWithNumber = correspondingImgWithNUmber.get_number();
                        */


                        if ( indx == touchCounter) {
                            Toast.makeText(context, "OK! marker number : " + indx, Toast.LENGTH_SHORT).show();

                                /* Doesn't work : try to draw number on markers
                                // show correct result drawing numebr on bitmap
                                Bitmap bitmap = img.get_bitmap();

                                // draw number on bitmap
                                myUtil.drawTextToBitmap(context, bitmap, Integer.toString(indx));

                                // store the bitmap modified with number
                                img.set_bitmap(bitmap);

                                //A paint object that does our drawing, on our canvas
                                Paint paint = new Paint();

                                //Canvas and Set the background color
                                Canvas canvas = new Canvas();
                                canvas.drawColor(Color.TRANSPARENT);

                                //Change the color of the virtual paint brush
                                paint.setColor(Color.argb(255, 1, 255, 255));

                                // draw on canvas marker with  number on them
                                canvas.drawBitmap(myUtil.resizeBitmapByScale(bitmap, imageScaleXY),
                                        randX, randY, paint);

                                this.invalidate();
                                */

                            img.set_touched(true); // marked image as touched
                            // win condition reached
                            if ( (allMarkerTouched() == true) && (touchCounter == itemNumber) ) {
                                touchResult.setValue(1);
                            }
                            touchCounter++;

                        }else {
                            // marked image as NOT touched: unnecessary, but make win condition check more robust
                            img.set_touched(false);
                            // Toast.makeText(context, "WRONG! marker img with number : " + indx_imgWithNumber, Toast.LENGTH_SHORT).show();
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
     * Avoid overlapping objects
     * NB : item origin is in left-top vertex
     *
     * @param x
     * @param y
     * @param size
     * @return ---------------------------------------------------------------------------------------------
     */
    public boolean isOverlapping(int x, int y, int size) {

        for (CircularImage circularImage : imgNumberList) {
            if (
                    (((x < circularImage.get_x()) && (x > circularImage.get_x() - size)) || ((x > circularImage.get_x()) && (x < circularImage.get_x() + size))) &&
                            (((y < circularImage.get_y()) && (y > circularImage.get_y() - size)) || ((y > circularImage.get_y()) && (y < circularImage.get_y() + size)))
            ) {
                return true;
            }
        }
        return false;

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
     *             ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth = w;
        mHeight = h;
        Log.d(TAG, "onCreate: mWidth : " + mWidth + " mHeigth : " + mHeight);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw view on screen with a defined items number
     *
     * @param itemNumber ---------------------------------------------------------------------------------------------
     */
    public void redraw(int itemNumber) {
        // this.timerLength = timerLength;
        this.itemNumber = itemNumber;
        this.invalidate();
    }

}

