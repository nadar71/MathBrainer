package eu.indiewalkabout.mathbrainer.aritmetic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.ui.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.statistics.Results;
import eu.indiewalkabout.mathbrainer.util.*;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

/**
 * ---------------------------------------------------------------------------------------------
 * Given a number, write its double
 * ---------------------------------------------------------------------------------------------
 */
public class DoubleNumberActivity extends AppCompatActivity implements IGameFunctions {

    final private UnityAdsListener unityAdsListener = new UnityAdsListener();

    // admob banner ref
    private AdView mAdView;

    private final static String TAG = DoubleNumberActivity.class.getSimpleName();

    // view ref
    private TextView             numberToBeDoubled_tv, scoreValue_tv, levelValue_tv,
                                 result_tv,operationSymbol_tv,
                                 scoreLabel_tv,
                                 highscore_label_tv,highscore_value_tv;


    private ArrayList<ImageView> lifesValue_iv ;

    private EditText             playerInput_et;

    private ImageView backhome_img;

    // store initial text color
    private ColorStateList quizDefaultTextColor;


    // number to be doubled
    private int numToBeDoubled;

    // starting level
    private int level = 0;

    // lifes counter; 0 to gameover
    private int lifes = 3;

    // random range of number to be doubled
    private int min = 1;
    private int max = 100;

    // num of challenge to be in the test
    private int numChallengeEachLevel =  10;
    private int countChallenge        =  1;

    // score var
    private int score = 0;

    // countdown objects
    ProgressBar countdownBar;
    CountDownIndicator countDownIndicator;

    // max time, increased by level growing
    private long timerLength            = CountDownIndicator.Companion.getDEFAULT_MILLISINFUTURE();
    private long timerCountDownInterval = CountDownIndicator.Companion.getDEFAULT_COUNTDOWNINTERVAL();

    // game session end dialog
    EndGameSessionDialog endSessiondialog;

    // custom keyboard instance
    MyKeyboard keyboard;

    // game over dialog
    GameOverDialog gameOverDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_number);

        // Unity ads init
        UnityAds.initialize(this,getResources().getString(R.string.unityads_key),unityAdsListener);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(DoubleNumberActivity.this));

        // set views ref
        numberToBeDoubled_tv   = (TextView)  findViewById(R.id.randomNum_tv);
        scoreValue_tv          = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv          = (TextView)  findViewById(R.id.levelValue_tv);
        playerInput_et         = (EditText)  findViewById(R.id.playerInput_et);
        operationSymbol_tv     = (TextView)  findViewById(R.id.answerDouble_tv);
        backhome_img           = (ImageView) findViewById(R.id.backhome_img);

        scoreLabel_tv          = (TextView) findViewById(R.id.scoreLabel_tv);
        highscore_label_tv     = (TextView) findViewById(R.id.high_scoreLabel_tv);
        highscore_value_tv     = (TextView) findViewById(R.id.high_scoreValue_tv);
        highscore_value_tv.setText("-1"); // debug init


        // show result tv
        result_tv = findViewById(R.id.result_tv);

        // store quiz text color for later use
        quizDefaultTextColor = numberToBeDoubled_tv.getTextColors();

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // keyboard
        setupCustomKeyboard();

        // countdown ref
        countdownBar = (ProgressBar)findViewById(R.id.progressbar);

        // Create new count down indicator, without starting it
        countDownIndicator = new CountDownIndicator(DoubleNumberActivity.this,
                (ProgressBar) countdownBar, DoubleNumberActivity.this);


        // start with first challenge and countdown init
        newChallenge();

        // set first level
        updateLevel();

        // set listener on DONE button on soft keyboard to get the player input
        playerInput_et.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPlayerInput();
                    return true;
                }
                return false;
            }
        });

        backhome_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // saves score
                isComingHome();

                // show unityads randomic
                MathBrainerUtility.INSTANCE.showUnityAdsRandom(DoubleNumberActivity.this);

                Intent intent = new Intent(DoubleNumberActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

        hideStatusNavBars();

        showHighscore();


    }


    @Override
    protected void onResume() {
        super.onResume();
        // make bottom navigation bar and status bar hide
        hideStatusNavBars();
        newChallenge();
    }



    @Override
    protected void onPause() {
        super.onPause();
        countDownIndicator.countdownReset();

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Set the highscore passed from main
     * ---------------------------------------------------------------------------------------------
     */
    private void showHighscore() {
        Intent intent = getIntent();
        if (intent.hasExtra(ChooseGameActivity.HIGHSCORE)) {
            int value = intent.getIntExtra(ChooseGameActivity.HIGHSCORE,-1);
            highscore_value_tv.setText(Integer.toString(value));
        } else {
            highscore_value_tv.setText("0");
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
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
     * ---------------------------------------------------------------------------------------------
     * Create  and setup customkeyboard
     * ---------------------------------------------------------------------------------------------
     */
    private void setupCustomKeyboard() {
        // init custom keyboard
        keyboard = (MyKeyboard) findViewById(R.id.keyboard);

        // prevent system keyboard from appearing when EditText is tapped
        playerInput_et.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = playerInput_et.getInputType(); // backup the input type
                playerInput_et.setInputType(InputType.TYPE_NULL); // disable soft input
                playerInput_et.onTouchEvent(event); // call native handler
                playerInput_et.setInputType(inType); // restore input type
                playerInput_et.setTextIsSelectable(false);
                return true; // consume touch even
            }
        });

        // pass the InputConnection from the EditText to the keyboard
        InputConnection ic = playerInput_et.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic, DoubleNumberActivity.this);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void checkPlayerInput() {
        int inputNum = 0;

        // get the player input
        String tmp = playerInput_et.getText().toString();

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return;
        }

        inputNum = Integer.parseInt(tmp);

        Log.d(TAG, "checkPlayerInput: inputNum : " + inputNum);

        // check if result is ok...
        if (inputNum != 0  && inputNum == 2*numToBeDoubled) {

            updateScore();

            countChallenge++;

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel){
                // reset counter
                countChallenge = 0;

                updateLevel();
            }

            // show result and start a new game session if allowed
            showResult(true);

        // ...otherwise a life will be lost
        } else {

            // lose a life, check if it's game over
            boolean gameover = isGameOver();

            // new number to double
            if (gameover == false) {
                // show result and start a new game session if allowed
                showResult(false);
            }

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check state at countdown expired
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void checkCountdownExpired() {

        // lose a life, check if it's game over
        boolean gameover = isGameOver();

        // new number to double
        if (gameover == false) {
            // show result and start a new game session if allowed
            showResult(false);
        }

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show the result of the
     * ---------------------------------------------------------------------------------------------
     */
    private void showResult(boolean win) {

        if (win == true) {
            showOkResult();
            newchallengeAfterTimerLength(1000);


        }else{
            showWrongResult();
            newchallengeAfterTimerLength(1000);

        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Show ok in case of game win
     * ---------------------------------------------------------------------------------------------
     */
    private void showOkResult() {
        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.ok_str));
        result_tv.setTextColor(Color.GREEN);
        numberToBeDoubled_tv.setTextColor(Color.GREEN);
        operationSymbol_tv.setTextColor(Color.GREEN);
        playerInput_et.setTextColor(Color.GREEN);
        // hide keyboard
        keyboard.setVisibility(View.INVISIBLE);

        // statistics
        Results.incrementGameResultsThread("operations_executed");
        Results.incrementGameResultsThread("operations_ok");

        Results.incrementGameResultsThread("doublings");
        Results.incrementGameResultsThread("multiplications");

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show wrong in case of game lose
     * ---------------------------------------------------------------------------------------------
     */
    private void showWrongResult() {
        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.wrong_str) + " : " + 2*numToBeDoubled);
        result_tv.setTextColor(Color.RED);
        numberToBeDoubled_tv.setTextColor(Color.RED);
        operationSymbol_tv.setTextColor(Color.RED);
        playerInput_et.setTextColor(Color.RED);
        // hide keyboard
        keyboard.setVisibility(View.INVISIBLE);

        // statistics
        Results.incrementGameResultsThread("operations_executed");
        Results.incrementGameResultsThread("operations_ko");

        Results.incrementGameResultsThread("doublings");
        Results.incrementGameResultsThread("multiplications");
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
                setupBeforeNewGame();
                newChallenge();
            }
        };
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Setup before new game session
     * ---------------------------------------------------------------------------------------------
     */
    private void setupBeforeNewGame() {
        result_tv.setVisibility(View.INVISIBLE);
        numberToBeDoubled_tv.setTextColor(quizDefaultTextColor);
        operationSymbol_tv.setTextColor(quizDefaultTextColor);
        playerInput_et.setTextColor(quizDefaultTextColor);
        keyboard.setVisibility(View.VISIBLE);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Update score view
     * ---------------------------------------------------------------------------------------------
     */
    private void updateScore() {
        highscore_label_tv.setVisibility(View.INVISIBLE);
        highscore_value_tv.setVisibility(View.INVISIBLE);
        scoreLabel_tv.setVisibility(View.VISIBLE);
        scoreValue_tv.setVisibility(View.VISIBLE);

        score += 25;
        scoreValue_tv.setText(Integer.toString(score));
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update lifes view and check if it's game over or not
     * @override of IGameFunctions isGameOver()
     * @return boolean  : return true/false in case of gameover/gamecontinuing
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean isGameOver() {
        // update life counts
        lifes--;

        // statistics
        Results.incrementGameResultsThread("lifes_missed");

        Log.d(TAG, "isGameOver: " + lifes);

        // Update UI
        if ( lifes > -1) { lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE); }

        // check game over condition
        if ( lifes <= 0){
            endGame();

            // statistics
            Results.incrementGameResultsThread("games_played");
            Results.incrementGameResultsThread("games_lose");
            Results.updateGameResultHighscoreThread("doublenumber_game_score", score);
            Results.incrementGameResultByDeltaThread("global_score", score);

            return true;

        }else {
            // lifes remaining >0, restart a new counter
            // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
            return false;
        }


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Saves the score and others game data if coming back home before reach game over condition
     * ---------------------------------------------------------------------------------------------
     */
    public void isComingHome() {
            Results.updateGameResultHighscoreThread("doublenumber_game_score", score);
            Results.incrementGameResultByDeltaThread("global_score", score);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Update progress bar
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void updateProgressBar(int progress) {
        countdownBar.setProgress(progress);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set new challenge in view
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void newChallenge() {
        // set the number to be doubled
        numToBeDoubled = MathBrainerUtility.INSTANCE.randRange_ApiCheck(min, max);
        numberToBeDoubled_tv.setText(Integer.toString(numToBeDoubled));

        // clean edit text field
        playerInput_et.isFocused();
        playerInput_et.setText("");
        Log.d(TAG, "newChallenge: " + countChallenge);

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Show end game message
     * ---------------------------------------------------------------------------------------------
     */
    private void endGame() {
        final Handler handler = new Handler();

        showWrongResult();

        // reset counter
        countDownIndicator.countdownReset();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                showGameOverDialog();
            }
        };
        handler.postDelayed(runnable, 500);
    }






    /**
     * ---------------------------------------------------------------------------------------------
     * Show gameover dialog
     * ---------------------------------------------------------------------------------------------
     */
    private void showGameOverDialog() {
        gameOverDialog = new GameOverDialog(this,
                DoubleNumberActivity.this, this);

        hideLastQuiz();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide quiz
     * ---------------------------------------------------------------------------------------------
     */
    private void hideLastQuiz() {
        playerInput_et.setVisibility(View.INVISIBLE);
        numberToBeDoubled_tv.setVisibility(View.INVISIBLE);
        operationSymbol_tv.setVisibility(View.INVISIBLE);
        operationSymbol_tv.setVisibility(View.INVISIBLE);
        result_tv.setVisibility(View.INVISIBLE);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Updating level
     * ---------------------------------------------------------------------------------------------
     */
    private void updateLevel(){
        // increment level
        level++;

        levelValue_tv.setText(Integer.toString(level));

        // increment level difficulty
        if (level > 1){
            min = max;
            max = 100 * level + 50 * (level - 1);

            // increase time accordingly, but slightly
            timerLength = timerLength + 1000 ;
            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "+max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

        // statistics
        Results.incrementGameResultsThread("level_upgrades");

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Unity ads listener
     * ---------------------------------------------------------------------------------------------
     */
    private class UnityAdsListener implements IUnityAdsListener{

        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {


            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    public void onBackPressed() {
        super.onBackPressed();
        // reset and destroy counter
        countDownIndicator.countdownReset();

        // saves score
        isComingHome();

        // show unityads randomic
        MathBrainerUtility.INSTANCE.showUnityAdsRandom(this);

    }



}
