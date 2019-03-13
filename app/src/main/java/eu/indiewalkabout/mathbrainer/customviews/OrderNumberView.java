package eu.indiewalkabout.mathbrainer.customviews;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.model.Item;
import eu.indiewalkabout.mathbrainer.util.myUtil;

/**
 * TODO: document your custom view class.
 */
public class OrderNumberView extends View {
    private static final String TAG =  QuickCountItemDrawView.class.getSimpleName();

    Context context;

    private float mWidth;                    // Custom view width
    private float mHeight;                   // Custom view height

    // number of items to be drawn
    private int itemNumber = 5;

    // scaling items images
    float imageScaleXY;

    // item coords
    int randX;
    int randY;

    // list of the items as pojo
    List<Item> itemList;

    // marker with no number upon
    List<Item> imgList;

    // marker with number upon
    // List<Bitmap> imgWithTextList;

    // time length before returning
    // int timerLength = 0;




    public OrderNumberView(Context context) {
        super(context);
        init(context);
    }

    public OrderNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OrderNumberView(Context context,
                           AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;

        imageScaleXY = 0.2f;

        itemList = new ArrayList<>();
        imgList  = new ArrayList<>();

        randX = (int)(mWidth*0.5);
        randY = (int)(mHeight*0.5);

        Log.d(TAG, "onCreate: mWidth : "+mWidth + " mHeigth : " + mHeight);


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
        itemList.clear();
        imgList.clear();

        // draw itemNumber images to count for
        for(int i=0;i<itemNumber;i++) {

                // ------------ collect first set of images, with number above ------------
                Resources res    = getResources();
                Bitmap    marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer);

                // get the img size (it's square) with scale
                int size = ( (int) ( (float)marker.getWidth()*imageScaleXY ) );
                int offsetFromBorder = 10+size;

                // make bitmap mutable for marker with number
                Bitmap imgWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true);

                // draw text on bitmap
                myUtil.drawTextToBitmap(context, imgWithNumber,Integer.toString(i));

                boolean isOverlap = true;

                while (isOverlap) {
                    randX = myUtil.randRange_ApiCheck(offsetFromBorder, (int) (mWidth - offsetFromBorder));
                    randY = myUtil.randRange_ApiCheck(offsetFromBorder, (int) (mHeight - offsetFromBorder));
                    isOverlap = isOverlapping(randX,randY, size);
                }

                // create a the image views

                // add to list of marker with number upon after overlapping is false
                Item tmp =  new Item(context,imgWithNumber,randX, randY, size);

                itemList.add(tmp);

                // create bitmap with no number upon it, and add to list
                Bitmap imgWithNoNumber = marker.copy(Bitmap.Config.ARGB_8888, true);
                imgList.add(new Item(context,imgWithNoNumber,randX, randY, size));


                // draw on canvas marker with no number on them
                canvas.drawBitmap(myUtil.resizeBitmapByScale(imgWithNoNumber, imageScaleXY),
                        randX, randY, paint);

                // draw on canvas marker with  number on them
                canvas.drawBitmap(myUtil.resizeBitmapByScale(imgWithNumber, imageScaleXY),
                        randX, randY, paint);

        }



    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Avoid overlapping objects
     * NB : item origin is in left-top vertex
     * @param x
     * @param y
     * @param size
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public boolean isOverlapping(int x, int y, int size){

        for(Item item:itemList){
            if (
                    ( ( (x < item.get_x()) && (x > item.get_x() - size) ) || ( (x > item.get_x()) && (x < item.get_x() + size) ) ) &&
                    ( ( (y < item.get_y()) && (y > item.get_y() - size) ) || ( (y > item.get_y()) && (y < item.get_y() + size) ) )
            )
            {
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
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth = w;
        mHeight = h;
        Log.d(TAG, "onCreate: mWidth : "+mWidth + " mHeigth : " + mHeight);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw view on screen with a defined items number
     * @param itemNumber
     * ---------------------------------------------------------------------------------------------
     */
    public void redraw(int itemNumber){
        // this.timerLength = timerLength;
        this.itemNumber = itemNumber;
        this.invalidate();
    }

}
