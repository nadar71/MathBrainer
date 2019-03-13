package eu.indiewalkabout.mathbrainer.othergames;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.customviews.MarkerWithNoNumberView;
import eu.indiewalkabout.mathbrainer.customviews.MarkerWithNumberView;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.myUtil;

public class NumberOrderActivity extends AppCompatActivity implements IGameFunctions {

    // admob banner ref
    private AdView mAdView;

    private static final String TAG =  CountObjectsActivity.class.getSimpleName();


    // Our costumview img references (almost useless)
    private ImageView ourFrame;

    // Costum views drawing items to count
    private MarkerWithNoNumberView drawquiz_challenge;
    private MarkerWithNumberView   drawquiz;


    private TextView scoreValue_tv, levelValue_tv, instructions_tv ;
    private ArrayList<ImageView> lifesValue_iv ;


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
    private int maxItemsToCount = 4;

    // items to count in current level
    private int itemsToCount    = maxItemsToCount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_numbers_order);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(NumberOrderActivity.this));

        // setup context
        context = this;

        //Get a reference to our ImageView in the layout
        ourFrame = (ImageView) findViewById(R.id.canvas_image_ref_img);

        // get the items to count view, set invisible at the moment
        drawquiz_challenge = findViewById(R.id.itemDrawingNoNumber_v);
        drawquiz = findViewById(R.id.itemDrawing_v);
        drawquiz.setVisibility(View.INVISIBLE);
        drawquiz_challenge.setVisibility(View.INVISIBLE);
        drawquiz_challenge.setImgNumberList(drawquiz.getImgwithNUmberList());

        // other views
        instructions_tv = findViewById(R.id.countobj_instructions_tv);
        btnNewGame      = findViewById(R.id.new_game_btn);
        btnNewGame.setVisibility(View.INVISIBLE);

        scoreValue_tv      = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv      = (TextView)  findViewById(R.id.levelValue_tv);

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // define wrong answers storage
        wrongAnswer = new ArrayList<>();

        newChallenge();

        // activate clicks on answer buttons
        setBtnPressedListener();

        // set first level
        updateLevel();

    }




    /**
     * -------------------------------------------------------------------------------------------------
     * Set up the button pressed listener and checking answers
     * -------------------------------------------------------------------------------------------------
     */
    private void setBtnPressedListener(){
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newChallenge();
            }
        });

    }

    /**
     * -------------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * -------------------------------------------------------------------------------------------------
     */
    private void checkPlayerInput() {

        Log.d(TAG, "checkPlayerInput: pressedBtnValue : " + pressedBtnValue);

        // check if result is ok...
        if (pressedBtnValue != 0  && pressedBtnValue == answerOK) {
            Toast.makeText(NumberOrderActivity.this, "OK!", Toast.LENGTH_SHORT).show();

            updateScore();

            countChallenge++;

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel){
                // reset counter
                countChallenge = 0;

                updateLevel();
            }

            // new number to double
            // newChallenge();
            newchallengeAfterTimerLength(1000);

            // ...otherwise a life will be lost
        } else {
            Toast.makeText(NumberOrderActivity.this, "WRONG...they are : "+answerOK, Toast.LENGTH_SHORT).show();


            // hideAnswerAfterTimerLength(1000);
            // lose a life, check if it's game over
            boolean gameover = isGameOver();

            // new number to double
            if (gameover == false) {
                // newChallenge();
                newchallengeAfterTimerLength(1000);
            }

        }
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
        lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE);


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
    private void newChallenge() {
        // clear wrong answers list
        wrongAnswer.clear();

        // show the items in number defined by level
        itemsToCount = myUtil.randRange_ApiCheck((int)Math.ceil(maxItemsToCount * 0.7),maxItemsToCount);
        drawquiz.redraw(itemsToCount);
        drawquiz_challenge.redraw(itemsToCount);

        // show items to count
        showItems();

        // hide and show button for answer
        // TODO : commented for debug
        hideQuizAfterTimerLength((int) timerLength);

        Log.d(TAG, "newChallenge: " + countChallenge);

        // set the answer buttons
        // setupAnswersBtn(itemsToCount);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void showItems(){
        btnNewGame.setVisibility(View.INVISIBLE);
        instructions_tv.setVisibility(View.INVISIBLE);
        drawquiz.setVisibility(View.VISIBLE);
        //drawquiz_challenge.setVisibility(View.VISIBLE);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void hideItems(){
        instructions_tv.setVisibility(View.VISIBLE);
        drawquiz.setVisibility(View.INVISIBLE);
        drawquiz_challenge.setVisibility(View.VISIBLE);

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
     * -------------------------------------------------------------------------------------------------
     * Show end game message
     * -------------------------------------------------------------------------------------------------
     */
    private void endGame() {


        // todo : game over screen
        Toast.makeText(NumberOrderActivity.this, "Congrats! Your score is : " + score
                + " on " + numChallengeEachLevel, Toast.LENGTH_LONG).show();

        instructions_tv.setVisibility(View.INVISIBLE);
        drawquiz.setVisibility(View.INVISIBLE);
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

            maxItemsToCount += 1;

            timerLength  += 0.5f;

            numChallengeEachLevel += 0;

            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "
                    +max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

    }

}
