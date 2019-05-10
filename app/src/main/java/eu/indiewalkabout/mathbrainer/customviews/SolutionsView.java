package eu.indiewalkabout.mathbrainer.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.customviews.model.CircularImage;
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility;

public class SolutionsView extends View {
    private static final String TAG = SolutionsView.class.getSimpleName();

    Context context;

    // scaling items images
    float imageScaleXY;


    // list of marker with number upon
    List<CircularImage> localSolutionsList;


    private float mWidth;                    // Custom view width
    private float mHeight;                   // Custom view height


    public SolutionsView(Context context) {
        super(context);
        init(context);
    }

    public SolutionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SolutionsView(Context context,
                                AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Rest the view state
     * ---------------------------------------------------------------------------------------------
     */
    private void init(Context context) {
        this.context = context;

        imageScaleXY = 0.2f;

        localSolutionsList = new ArrayList<>();

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Start over new game
     * ---------------------------------------------------------------------------------------------
     */
    public void resetGame(){
        // clear from previous items
        localSolutionsList.clear();
    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Set solution list from MarkerWithNoNumber to localSolutionsList
     * ---------------------------------------------------------------------------------------------
     */
    public void setSolutionList(List<CircularImage> localSolutionsList){
        this.localSolutionsList = localSolutionsList;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // resetGame();

        //A paint object that does our drawing, on our canvas
        Paint paint = new Paint();

        //Set the background color
        canvas.drawColor(Color.TRANSPARENT);

        //Change the color of the virtual paint brush
        paint.setColor(Color.argb(255, 1, 255, 255));

        // draw solutions images currently discovered taken from MarkerWithNoNumber
        if (localSolutionsList.size() > 0) {
            for (int i = 0; i < localSolutionsList.size(); i++) {

                CircularImage solutionImg = localSolutionsList.get(i);

                // draw new marker with corrispondent number on it
                Resources res = getResources();
                Bitmap marker = BitmapFactory.decodeResource(res, R.drawable.number_pointer_green);

                // make bitmap mutable for marker with number
                Bitmap bitmapWithNumber = marker.copy(Bitmap.Config.ARGB_8888, true);

                // draw text on bitmap
                MathBrainerUtility.drawTextToBitmap(context, bitmapWithNumber,
                        Integer.toString(solutionImg.get_number()));


                // draw on canvas marker with  number on them
                canvas.drawBitmap(MathBrainerUtility.resizeBitmapByScale(bitmapWithNumber, imageScaleXY),
                        solutionImg.get_x(), solutionImg.get_y(), paint);
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
     *             ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Calculate the radius from the width and height.
        mWidth  = w;
        mHeight = h;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Draw view on screen with a defined items number
     * ---------------------------------------------------------------------------------------------
     */
    public void redraw() {
        this.invalidate();
    }
}
