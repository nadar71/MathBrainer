package eu.indiewalkabout.mathbrainer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.util.*;

/**
 * -------------------------------------------------------------------------------------------------
 * Given a number, write its double
 * -------------------------------------------------------------------------------------------------
 */
public class DoubleNumberActivity extends AppCompatActivity {

    // tag for log
    private final static String TAG = DoubleNumberActivity.class.getSimpleName();

    // view ref
    private TextView             numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;
    private ArrayList<ImageView> lifesValue_iv ;
    // private ImageView lifesValue_iv_01, lifesValue_iv_02, lifesValue_iv_03;
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
    private int numChallengeEachLevel =  3;
    private int countChallenge        =  1;

    // score var
    private int score = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_number);

        // set views ref
        numberToBeDoubled_tv   = (TextView)  findViewById(R.id.randomNum_tv);
        scoreValue_tv          = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv          = (TextView)  findViewById(R.id.levelValue_tv);
        playerInput_et         = (EditText)  findViewById(R.id.playerInput_et);

        // init lifes led images
        //*
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));
        //*/
        /*
        lifesValue_iv_01 = (ImageView) findViewById(R.id.life_01_iv);
        lifesValue_iv_02 = (ImageView) findViewById(R.id.life_02_iv);
        lifesValue_iv_03 = (ImageView) findViewById(R.id.life_03_iv);
        */


        // start with first challenge
        newChallenge();

        // set first level
        updateLevel();

        // set listner on DONE button on soft keyboard to get the player input
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
     * Check if player input is right/wrong and update score
     * -------------------------------------------------------------------------------------------------
     */
    private void checkPlayerInput() {
        int inputNum = 0;

        // get the player input
        inputNum = Integer.parseInt(playerInput_et.getText().toString());

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

            // new number to double
            newChallenge();

        // ...otherwise a life will be lost
        } else {
            Toast.makeText(DoubleNumberActivity.this, "WRONG...", Toast.LENGTH_SHORT).show();

            // lose a life
            updateLifes();

            // new number to double
            newChallenge();

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
     * Update lifes view
     * -------------------------------------------------------------------------------------------------
     */
    private void updateLifes() {
        lifes--;

        Log.d(TAG, "updateLifes: " + lifes);
        // check game over condition
        if ( lifes <= 0){
            // hide input field
            playerInput_et.setVisibility(View.INVISIBLE);

            // hide keyboard
            InputMethodManager inpMng = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inpMng.hideSoftInputFromWindow(playerInput_et.getWindowToken(), 0);

            endGame();

        }
        // todo : hide images/change color
    }

    /**
     * -------------------------------------------------------------------------------------------------
     * Set new challenge in view
     * -------------------------------------------------------------------------------------------------
     */
    private void newChallenge() {
        // set the number to be doubled
        numToBeDoubled = myUtil.randRange_ApiCheck(min, max);
        numberToBeDoubled_tv.setText(Integer.toString(numToBeDoubled));

        // clean edit text field
        playerInput_et.isFocused();
        playerInput_et.setText("");
        Log.d(TAG, "newChallenge: " + countChallenge);

    }


    /**
     * -------------------------------------------------------------------------------------------------
     * Show end game message
     * -------------------------------------------------------------------------------------------------
     */
    private void endGame() {
        Toast.makeText(DoubleNumberActivity.this, "Congrats! Your score is : " + score + " on " + numChallengeEachLevel, Toast.LENGTH_LONG).show();

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
        if (level > 1){
            min = max;
            max = 100 * level + 50 * (level - 1);
            Log.d(TAG, "updatingLevel: new min : "+min+" new max: "+max+" new level : "+level);
        }

    }

}
