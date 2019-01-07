package eu.indiewalkabout.mathbrainer.othergames;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.ProgressBar;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.myUtil;

public class CountObjectsActivity extends AppCompatActivity  implements IGameFunctions {

    private static final String TAG =  CountObjectsActivity.class.getSimpleName();


    // Our draw frame
    ImageView ourFrame;

    // countdown objects
    ProgressBar countdownBar;
    CountDownIndicator countDownIndicator;


    // max time, increased by level growing
    private long timerLength            = 1000;
    private long timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_count_objects);

        //Get a reference to our ImageView in the layout
        ourFrame = (ImageView) findViewById(R.id.imageView);

        // countdown ref
        countdownBar = (ProgressBar)findViewById(R.id.progressbar);

        // Create new count down indicator, without starting it
        countDownIndicator = new CountDownIndicator(CountObjectsActivity.this, (ProgressBar) countdownBar, CountObjectsActivity.this);

        // set gone the countdown bar
        countDownIndicator.goneCountDownbar();

        newChallenge();


    }


    // ---------------------------------------------------------------------------------------------
    // Get the width of the screen
    // ---------------------------------------------------------------------------------------------
    public  int getScreenWidth() {

        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // ---------------------------------------------------------------------------------------------
    // Get the height of the screen
    // ---------------------------------------------------------------------------------------------
    public  int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Set new challenge in view
     * ---------------------------------------------------------------------------------------------
     */
    private void newChallenge() {

        // DEBUG
        drawGame();

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);



    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Update progress bar
     * -------------------------------------------------------------------------------------------------
     */
    @Override
    public void updateProgressBar(int progress) {
        countdownBar.setProgress(progress);
    }



    /**
     * -------------------------------------------------------------------------------------------------
     * Update lifes view and check if it's game over or not
     * @override of IGameFunctions isGameOver()
     * @return boolean  : return true/false in case of gameover/gamecontinuing
     * -------------------------------------------------------------------------------------------------
     */
    @Override
    public boolean isGameOver() {

        /*
        // update life counts
        lifes--;

        Log.d(TAG, "isGameOver: " + lifes);

        // Update UI
        lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE);

        // check game over condition
        if ( lifes <= 0){
            // hide input field
            playerInput_et.setVisibility(View.INVISIBLE);

            // hide keyboard
            InputMethodManager inpMng = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inpMng.hideSoftInputFromWindow(playerInput_et.getWindowToken(), 0);

            endGame();
            return true;

        }else {
            // lifes remaining >0, restart a new counter
            countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
            return false;
        }
        */
        newChallenge();
        return true;

    }



    // ---------------------------------------------------------------------------------------------
    //                                          DEBUG
    // ---------------------------------------------------------------------------------------------
    // -------------------------------------------
    // Scratch method for testing drawing method
    // -------------------------------------------
    private void drawGame() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth  = size.x;
        int screenHeight = size.y;

        int validDrawAreaWidth  = (int) (screenWidth  * 1.05);
        int validDrawAreaHeight = (int) (screenHeight * 0.8);

        // int validDrawAreaItemsWidth  = (int) (screenWidth  * 0.5);
        // int validDrawAreaItemsHeight = (int) (screenHeight * 0.5);

        int validDrawAreaItemsWidth  = (int) (getScreenWidth()  * 0.95);
        int validDrawAreaItemsHeight = (int) (getScreenHeight() * 0.72);

        float imageScaleXY = 0.08f;

        //Create a bitmap object to use as our canvas
        Log.d(TAG, "onCreate: validDrawAreaItemsWidth : "+validDrawAreaItemsWidth + " validDrawAreaItemsHeight : " + validDrawAreaItemsHeight);

        Bitmap ourBitmap = Bitmap.createBitmap(validDrawAreaWidth,validDrawAreaHeight, Bitmap.Config.ARGB_8888);

        //Assign the bitmap to image = our frame
        ourFrame.setImageBitmap(ourBitmap);

        // Assign the bitmap to the canvas
        Canvas ourCanvas = new Canvas(ourBitmap);

        //A paint object that does our drawing, on our canvas
        Paint paint = new Paint();

        //Set the background color
        ourCanvas.drawColor(Color.TRANSPARENT);
        // ourCanvas.drawColor(Color.argb(0, 0, 0, 255));

        //Change the color of the virtual paint brush
        paint.setColor(Color.argb(255, 1, 255, 255));


        // draw numebr of images to remember
        Bitmap a = BitmapFactory.decodeResource(getResources(), R.drawable.memo101);
        a = a.copy(Bitmap.Config.ARGB_8888, true);
        int randX = myUtil.randRange_ApiCheck(15, validDrawAreaItemsWidth);
        int randY = myUtil.randRange_ApiCheck(15, validDrawAreaItemsHeight);
        ourCanvas.drawBitmap(myUtil.resizeBitmapByScale(a, imageScaleXY,false, 100, 100 ), randX, randY, paint);


        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.memo102);
        b = b.copy(Bitmap.Config.ARGB_8888, true);
        randX = myUtil.randRange_ApiCheck(15, validDrawAreaItemsWidth);
        randY = myUtil.randRange_ApiCheck(15, validDrawAreaItemsHeight);
        ourCanvas.drawBitmap(myUtil.resizeBitmapByScale(b, imageScaleXY,false, 100, 100 ), randX, randY, paint);


        // invalidate view for redrawing
        ourFrame.invalidate();
    }












}
