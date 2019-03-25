package eu.indiewalkabout.mathbrainer.aritmetic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.*;

/**
 * -------------------------------------------------------------------------------------------------
 * Given a number, write its double
 * -------------------------------------------------------------------------------------------------
 */
public class DoubleNumberActivity extends AppCompatActivity implements IGameFunctions {

    // admob banner ref
    private AdView mAdView;

    // tag for log
    private final static String TAG = DoubleNumberActivity.class.getSimpleName();

    // view ref
    private TextView             numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;
    private ArrayList<ImageView> lifesValue_iv ;
    private EditText             playerInput_et;


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
    private long timerLength            = CountDownIndicator.DEFAULT_MILLISINFUTURE;
    private long timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL;

    // game session end dialog
    EndGameSessionDialog endSessiondialog;

    // custom keyboard instance
    MyKeyboard keyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_number);


        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(DoubleNumberActivity.this));

        // set views ref
        numberToBeDoubled_tv   = (TextView)  findViewById(R.id.randomNum_tv);
        scoreValue_tv          = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv          = (TextView)  findViewById(R.id.levelValue_tv);
        playerInput_et         = (EditText)  findViewById(R.id.playerInput_et);

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
        countDownIndicator = new CountDownIndicator(DoubleNumberActivity.this, (ProgressBar) countdownBar, DoubleNumberActivity.this);


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


    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Create  and setup customkeyboard
     * -------------------------------------------------------------------------------------------------
     */
    private void setupCustomKeyboard() {
        // init custom keyboard
        keyboard = (MyKeyboard) findViewById(R.id.keyboard);

        // prevent system keyboard from appearing when EditText is tapped
        playerInput_et.setRawInputType(InputType.TYPE_CLASS_TEXT);
        playerInput_et.setTextIsSelectable(true);

        // pass the InputConnection from the EditText to the keyboard
        InputConnection ic = playerInput_et.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic, DoubleNumberActivity.this);
    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * -------------------------------------------------------------------------------------------------
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
            Toast.makeText(DoubleNumberActivity.this, "OK!", Toast.LENGTH_SHORT).show();

            updateScore();

            countChallenge++;

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel){
                // reset counter
                countChallenge = 0;

                updateLevel();
            }

            endSessiondialog = new EndGameSessionDialog(this,
                    DoubleNumberActivity.this,
                    EndGameSessionDialog.GAME_SESSION_RESULT.OK);

            // newChallenge();

        // ...otherwise a life will be lost
        } else {
            Toast.makeText(DoubleNumberActivity.this, "WRONG...", Toast.LENGTH_SHORT).show();

            // lose a life, check if it's game over
            boolean gameover = isGameOver();

            // new number to double
            if (gameover == false) {
                // newChallenge();
                endSessiondialog = new EndGameSessionDialog(this,
                        DoubleNumberActivity.this,
                        EndGameSessionDialog.GAME_SESSION_RESULT.WRONG);
            }

        }
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
            // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
            return false;
        }


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
        numToBeDoubled = myUtil.randRange_ApiCheck(min, max);
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
        // reset counter
        countDownIndicator.countdownReset();

        // todo : game over screen
        Toast.makeText(DoubleNumberActivity.this, "Congrats! Your score is : " + score + " on " + numChallengeEachLevel, Toast.LENGTH_LONG).show();

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

            // TODO : decomment to activate interstitial ads
            /*
            // show interstitial ad on back home only 50% of times
            int guess = GenericUtility.randRange_ApiCheck(1,10);
            if (guess <=4) {
                showInterstitialAd();
            }
            */

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

    }



}
