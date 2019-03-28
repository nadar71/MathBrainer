package eu.indiewalkabout.mathbrainer.othergames;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.customviews.QuickCountItemDrawView;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.EndGameSessionDialog;
import eu.indiewalkabout.mathbrainer.util.GameOverDialog;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.myUtil;

public class CountObjectsActivity extends AppCompatActivity implements  IGameFunctions {

    // admob banner ref
    private AdView mAdView;

    private static final String TAG =  CountObjectsActivity.class.getSimpleName();


    // Our costumview img references (almost useless)
    private ImageView ourFrame;

    // Costum view drawing items to count
    private QuickCountItemDrawView drawquiz;


    private TextView scoreValue_tv, levelValue_tv, instructions_tv, count_obj_instructions_tv,
                     result_tv;
    private GridLayout buttonGrid;
    private ArrayList<ImageView> lifesValue_iv ;
    private ImageView backhome_img;


    private Button answer01Btn, answer02Btn, answer03Btn, answer04Btn;
    private Button btnNewGame;

    // answer and its stuff
    private int answerOK;
    private int correctBtnNumber = 1;
    private int offset           = 10;
    private int pressedBtnValue  = 0;

    // starting level
    private int level = 0;

    // lifes counter; 0 to gameover
    private int lifes = 3;

    // random range of offset from correct answer
    private int min        = 1;
    private int max        = 6;


    // store wring answer to avoid duplicates
    ArrayList<Integer> wrongAnswer;

    // num of challenge to pass to next level
    // changing while level growing
    private int numChallengeEachLevel =  1;
    private int countChallenge        =  1;

    // random range for answer btn number
    // changing while level growing
    private int minAnswerBtnNum   = 1;
    private int maxAnswerBtnNum   = 4;

    // score var
    private int score = 0;

    // Context
    private Context context;

    // max time to show the items to count
    private float timerLength  = 2000f;

    // max num range items to count for challenge
    private int maxItemsToCount = 6;

    // items to count in current level
    private int itemsToCount    = maxItemsToCount;

    // game session end dialog
    EndGameSessionDialog endSessiondialog;

    // game over dialog
    GameOverDialog gameOverDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_count_objects);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(CountObjectsActivity.this));

        // setup context
        context = this;

        //Get a reference to our ImageView in the layout
        ourFrame = (ImageView) findViewById(R.id.canvas_image_ref_img);

        // get the items to count view, set inviisible at the moment
        drawquiz = findViewById(R.id.itemDrawing_v);
        drawquiz.setVisibility(View.INVISIBLE);

        // other views
        instructions_tv = findViewById(R.id.quiz_instructions_tv);
        buttonGrid      = findViewById(R.id.answerBtnGrid);
        btnNewGame      = findViewById(R.id.new_game_btn);
        result_tv       = findViewById(R.id.result_tv);
        backhome_img    = (ImageView)  findViewById(R.id.backhome_img);

        btnNewGame.setVisibility(View.INVISIBLE);
        result_tv.setVisibility(View.INVISIBLE);

        scoreValue_tv             = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv             = (TextView)  findViewById(R.id.levelValue_tv);
        count_obj_instructions_tv = (TextView)  findViewById(R.id.count_obj_instructions_tv);

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // btn references
        answer01Btn =  findViewById(R.id.answer_01_btn);
        answer02Btn =  findViewById(R.id.answer_02_btn);
        answer03Btn =  findViewById(R.id.answer_03_btn);
        answer04Btn =  findViewById(R.id.answer_04_btn);

        // define wrong answers storage
        wrongAnswer = new ArrayList<>();


        newChallenge();

        // activate clicks on answer buttons
        setBtnPressedListener();

        // set first level
        updateLevel();

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();

    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * -------------------------------------------------------------------------------------------------
     */
    private void hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar
                        // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // // hide status bar
        decorView.setSystemUiVisibility(uiOptions);
    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Set up the button pressed listener and checking answers
     * -------------------------------------------------------------------------------------------------
     */
    private void setBtnPressedListener(){
        answer01Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer02Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer03Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer04Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newChallenge();
            }
        });

        backhome_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CountObjectsActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * -------------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * -------------------------------------------------------------------------------------------------
     */
    @Override
    public void checkPlayerInput() {

        Log.d(TAG, "checkPlayerInput: pressedBtnValue : " + pressedBtnValue);

        // check if result is ok...
        if (pressedBtnValue != 0  && pressedBtnValue == answerOK) {

            updateScore();

            countChallenge++;

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel){
                // reset counter
                countChallenge = 0;

                updateLevel();
            }

            showNewBtnAfterTimerLength(100, true);

            // ...otherwise a life will be lost

        } else {

            // hideAnswerAfterTimerLength(1000);
            // lose a life, check if it's game over
            boolean gameover = isGameOver();

            if (gameover == false) {
                showNewBtnAfterTimerLength(100, false);
            }

        }
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Show button for new challenge after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private void showNewBtnAfterTimerLength(final int timerLength, final boolean win){
        buttonGrid.setVisibility(View.INVISIBLE);
        instructions_tv.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                btnNewGame.setVisibility(View.VISIBLE);
                instructions_tv.setText("   ");
                result_tv.setVisibility(View.VISIBLE);
                if (win == false) {
                    result_tv.setText(getResources().getString(R.string.wrong_str));
                    result_tv.setTextColor(Color.RED);
                } else if (win == true){
                    result_tv.setText(getResources().getString(R.string.ok_str));
                    result_tv.setTextColor(Color.GREEN);
                }

            }
        };
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide answer as items group after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private void hideAnswerAfterTimerLength(final int timerLength){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                drawquiz.setVisibility(View.INVISIBLE);
            }
        };
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Launch new challenge after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private void newchallengeAfterTimerLength(final int timerLength){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                buttonGrid.setVisibility(View.INVISIBLE);
                instructions_tv.setVisibility(View.INVISIBLE);
                btnNewGame.setVisibility(View.VISIBLE);

            }
        };
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength);
    }





    /**
     * -------------------------------------------------------------------------------------------------
     * Update score view
     * -------------------------------------------------------------------------------------------------
     */
    private void updateScore() {
        score += 25;
        scoreValue_tv.setText(Integer.toString(score));
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
        Log.d(TAG, "isGameOver: " + lifes);

        // update life counts
        lifes--;


        // Update UI
        if ( lifes > -1) { lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE);}


        // check game over condition
        if ( lifes <= 0){

            endGame();
            return true;

        }

        return false;

    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    public void updateProgressBar(int progress){

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Set new challenge in view
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void newChallenge() {
        // clear wrong answers list
        wrongAnswer.clear();

        result_tv.setVisibility(View.INVISIBLE);

        // show the items in number defined by level
        itemsToCount = myUtil.randRange_ApiCheck((int)Math.ceil(maxItemsToCount * 0.7),maxItemsToCount);
        drawquiz.redraw(itemsToCount);

        // show items to count
        showItems();

        // hide and show button for answer
        hideQuizAfterTimerLength((int) timerLength);

        Log.d(TAG, "newChallenge: " + countChallenge);

        // set the answer buttons
        setupAnswersBtn(itemsToCount);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void showItems(){
        btnNewGame.setVisibility(View.INVISIBLE);
        buttonGrid.setVisibility(View.INVISIBLE);
        instructions_tv.setVisibility(View.INVISIBLE);
        drawquiz.setVisibility(View.VISIBLE);
        count_obj_instructions_tv.setVisibility(View.VISIBLE);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void hideItems(){
        buttonGrid.setVisibility(View.VISIBLE);
        instructions_tv.setVisibility(View.VISIBLE);
        instructions_tv.setText(getResources().getString(R.string.count_objects_question));
        drawquiz.setVisibility(View.INVISIBLE);
        count_obj_instructions_tv.setVisibility(View.INVISIBLE);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide items group after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private void hideQuizAfterTimerLength(final int timerLength){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hideItems();
            }
        };
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength);
    }





    /**
     * ---------------------------------------------------------------------------------------------
     * Create setup correct answer and false answer on buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void setupAnswersBtn(int numItemsToCount) {

        // set the correct answer
        answerOK = itemsToCount;

        // choose the button where put the correct answer
        correctBtnNumber = myUtil.randRange_ApiCheck(minAnswerBtnNum, maxAnswerBtnNum);
        Button tmpBtn    = getTheBtnNumber(correctBtnNumber);
        tmpBtn.setText(Integer.toString(answerOK));


        // set wrong answer on the others
        for(int i = 1; i <= maxAnswerBtnNum; i++){
            if (i != correctBtnNumber){

                tmpBtn = getTheBtnNumber(i);
                int result = 0;
                do {
                    result = randomOffsetSum();
                } while (wrongAnswer.lastIndexOf(result) > 0);
                wrongAnswer.add(result);

                tmpBtn.setText(String.valueOf(result));

            }else { // the btn with the right answer must be alwys visible
                tmpBtn = getTheBtnNumber(correctBtnNumber);
            }
        }
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    private int randomOffsetSum(){
        int result = myUtil.randRange_ApiCheck(1, (int)(offset * 1.0));
        if ( (result >= 1) && (result <= 3) ) {
            int sign = myUtil.randomSignChooser();
            return answerOK + sign * result;
        }
        return answerOK + result;
    }





    /**
     * ---------------------------------------------------------------------------------------------
     * Return the button based on number
     * @param num
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    Button getTheBtnNumber(int num){
        switch(num){
            case 1 : return answer01Btn;
            case 2 : return answer02Btn;
            case 3 : return answer03Btn;
            case 4 : return answer04Btn;
            default: break;
        }
        return null;
    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Show end game message
     * -------------------------------------------------------------------------------------------------
     */
    private void endGame() {
        final Handler handler = new Handler();

        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.wrong_str));
        result_tv.setTextColor(Color.RED);

        buttonGrid.setVisibility(View.INVISIBLE);
        instructions_tv.setVisibility(View.INVISIBLE);
        drawquiz.setVisibility(View.INVISIBLE);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showGameOverDialog();
            }
        };
        handler.postDelayed(runnable, 500);

    }








    /**
     * -------------------------------------------------------------------------------------------------
     * Show gameover dialog
     * -------------------------------------------------------------------------------------------------
     */
    private void showGameOverDialog() {
        gameOverDialog = new GameOverDialog(this,
                CountObjectsActivity.this, this);

        result_tv.setVisibility(View.INVISIBLE);
    }

    /**
     * -------------------------------------------------------------------------------------------------
     * Updating level
     * -------------------------------------------------------------------------------------------------
     */
    private void updateLevel(){
        // increment level
        level++;

        levelValue_tv.setText(Integer.toString(level));

        // increment level difficulty
        if ( (level > 1) && (level < 30)){

            maxItemsToCount += 2;

            timerLength  += 0.5f;

            numChallengeEachLevel += 0;

            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "
                    +max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Check state at countdown expired
     * -------------------------------------------------------------------------------------------------
     */
    @Override
    public void checkCountdownExpired() {

        // lose a life, check if it's game over
        boolean gameover = isGameOver();

        // new number to double
        if (gameover == false) {
            // newchallengeAfterTimerLength(1000);
            buttonGrid.setVisibility(View.INVISIBLE);
            instructions_tv.setVisibility(View.INVISIBLE);
            endSessiondialog = new EndGameSessionDialog(this,
                    CountObjectsActivity.this,
                    EndGameSessionDialog.GAME_SESSION_RESULT.WRONG);
        }

    }




// -------------------------------------------------------------------------------------------------
//                                    UNUSED STUFF, just for debug
// -------------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // UNUSED , just for debug
    // Get the width of the screen
    // ---------------------------------------------------------------------------------------------
    public  int getScreenWidth() {

        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    // ---------------------------------------------------------------------------------------------
    // UNUSED , just for debug
    // Get the height of the screen
    // ---------------------------------------------------------------------------------------------
    public  int getScreenHeight() {

        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }



    // -------------------------------------------
    // UNUSED , just for debug
    // Draw game items to count
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

        float imageScaleXY = 0.2f;

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


        // draw 20 images
        for(int i=0;i<20;i++) {
            try {
                // get images
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("memo" + (100+i) + ".png");
                Bitmap item = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                // Log.d("BitmapText","memo" + (100+i) + ".png format: " + item.getConfig());

                // draw on canvas
                item = item.copy(Bitmap.Config.ARGB_8888, true);
                int randX = myUtil.randRange_ApiCheck(15, validDrawAreaItemsWidth);
                int randY = myUtil.randRange_ApiCheck(15, validDrawAreaItemsHeight);
                ourCanvas.drawBitmap(myUtil.resizeBitmapByScale(item, imageScaleXY),
                        randX, randY, paint);

            } catch (IOException e) {
                Log.d(TAG, "drawGame: "+e.getMessage());
            } finally {
                // we should really close our input streams here.
            }
        }


        /*
        // dummy check from drawable
        Bitmap a = BitmapFactory.decodeResource(getResources(), R.drawable.memo101);
        a = a.copy(Bitmap.Config.ARGB_8888, true);
        int randX = myUtil.randRange_ApiCheck(15, validDrawAreaItemsWidth);
        int randY = myUtil.randRange_ApiCheck(15, validDrawAreaItemsHeight);
        ourCanvas.drawBitmap(myUtil.resizeBitmapByScale(a, imageScaleXY, false, 100, 100), randX, randY, paint);
        */

        // invalidate view for redrawing
        ourFrame.invalidate();
    }





}
