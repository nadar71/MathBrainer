package eu.indiewalkabout.mathbrainer.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.model.CircularImage;
import eu.indiewalkabout.mathbrainer.util.myUtil;


public class MarkerWithNumberView extends View {
    private static final String TAG = MarkerWithNumberView.class.getSimpleName();

    Context context;

    // scaling items images
    float imageScaleXY;

    // item coords
    int randX;
    int randY;

    // list of marker with number upon
    List<CircularImage> imgNumberList;


    private float mWidth;                    // Custom view width
    private float mHeight;                   // Custom view height

    // number of items to be drawn, modificed in redrawn function
    private int itemNumber = 5;


    public MarkerWithNumberView(Context context) {
        super(context);
        init(context);
    }

    public MarkerWithNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarkerWithNumberView(Context context,
                                AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public List<CircularImage> getImgwithNUmberList(){
        return  imgNumberList;
    }


    private void init(Context context) {
        this.context = context;

        imageScaleXY = 0.2f;

        imgNumberList = new ArrayList<>();

        randX = (int) (mWidth * 0.5);
        randY = (int) (mHeight * 0.5);

        Log.d(TAG, "onCreate: mWidth : " + mWidth + " mHeigth : " + mHeight);

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
        imgNumberList.clear();


        // draw itemNumber images to count for
        for (int i = 0; i < itemNumber; i++) {

            // ------------ collect first set of images, with number above ------------
            Resources res = getResources();
            Bitmap marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer);

            // get the img size (it's square) with scale
            int size = ((int) ((float) marker.getWidth() * imageScaleXY));
            int offsetFromBorder = 10 + size;

            // make bitmap mutable for marker with number
            Bitmap bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true);

            // draw text on bitmap
            myUtil.drawTextToBitmap(context, bitmapWithNumber, Integer.toString(i));

            boolean isOverlap = true;

            while (isOverlap) {
                randX = myUtil.randRange_ApiCheck(offsetFromBorder, (int) (mWidth - offsetFromBorder));
                randY = myUtil.randRange_ApiCheck(offsetFromBorder, (int) (mHeight - offsetFromBorder));
                isOverlap = isOverlapping(randX, randY, size);
            }

            // create an image view
            CircularImage imgWithNumber = new CircularImage(context, randX, randY, size);
            imgWithNumber.setImageBitmap(bitmapWithNumber);
            imgWithNumber.set_number(i);
            imgNumberList.add(imgWithNumber);

            // draw on canvas marker with  number on them
            canvas.drawBitmap(myUtil.resizeBitmapByScale(bitmapWithNumber, imageScaleXY),
                    randX+50, randY, paint);

        }
        Log.d(TAG, "onDraw: ");

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
