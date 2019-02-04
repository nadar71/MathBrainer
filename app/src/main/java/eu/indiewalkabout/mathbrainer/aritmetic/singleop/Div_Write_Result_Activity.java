package eu.indiewalkabout.mathbrainer.aritmetic.singleop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.myUtil;

/**
 * -------------------------------------------------------------------------------------------------
 * Same game as MixedOp_Write_Result_Activity, apart that is only about divisions
 * Changed only the symbols array to have only : "/"
 * -------------------------------------------------------------------------------------------------
 */
public class Div_Write_Result_Activity extends AppCompatActivity implements IGameFunctions {


    // admob banner ref
    private AdView mAdView;

    // tag for log
    private final static String TAG = Div_Write_Result_Activity.class.getSimpleName();

    // view ref
    private TextView numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;
    private TextView firstOperand_tv, secondOperand_tv, operationSymbol_tv;
    private ArrayList<ImageView> lifesValue_iv ;
    private EditText playerInput_et;


    // numbers to be processed
    private int  firstOperand, secondOperand;
    private char operation;

    // correct answer
    private int answerOK;

    // starting level
    private int level = 0;

    // lifes counter; 0 to gameover
    private int lifes = 3;

    // random range of number to be processed
    private int min        = 1;
    private int max        = 100;


    private int multMin    = 1;
    private int multHMax   = 30;
    private int multLMax   = 15;

    private int divMin     = 1;
    private int divHMax    = 15;
    private int divLMax    = 11;

    private char[] symbols = {'/'};

    // num of challenge to pass to next level
    // changing while level growing
    private int numChallengeEachLevel =  25;
    private int countChallenge        =  1;

    // score var
    private int score = 0;

    // countdown objects
    ProgressBar countdownBar;
    CountDownIndicator countDownIndicator;

    // max time, increased by level growing
    private long timerLength            = 20000;
    private long timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_div_write_result);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(Div_Write_Result_Activity.this));

        // set views ref
        firstOperand_tv    = (TextView)  findViewById(R.id.firstOperand_tv);
        secondOperand_tv   = (TextView)  findViewById(R.id.secondOperand_tv);
        operationSymbol_tv = (TextView)  findViewById(R.id.operationSymbol_tv);

        scoreValue_tv      = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv      = (TextView)  findViewById(R.id.levelValue_tv);
        playerInput_et     = (EditText)  findViewById(R.id.playerInput_et);

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // countdown ref
        countdownBar = (ProgressBar)findViewById(R.id.progressbar);

        // Create new count down indicator, without starting it
        countDownIndicator = new CountDownIndicator(Div_Write_Result_Activity.this, (ProgressBar) countdownBar, Div_Write_Result_Activity.this);


        // start with first challenge and countdown init
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
        String tmp = playerInput_et.getText().toString();

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return;
        }

        inputNum = Integer.parseInt(tmp);

        Log.d(TAG, "checkPlayerInput: inputNum : " + inputNum);

        // check if result is ok...
        if (inputNum != 0  && inputNum == answerOK) {
            Toast.makeText(Div_Write_Result_Activity.this, "OK!", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(Div_Write_Result_Activity.this, "WRONG...", Toast.LENGTH_SHORT).show();

            // lose a life, check if it's game over
            boolean gameover = isGameOver();

            // new number to double
            if (gameover == false) {
                newChallenge();
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
            countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
            return false;
        }


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
     * Set new challenge in view
     * -------------------------------------------------------------------------------------------------
     */
    private void newChallenge() {
        // set operation to be processed
        operation    = symbols[myUtil.randRange_ApiCheck(0, symbols.length-1)];

        // calculate the quiz operation
        calculateOperation();

        // clean edit text field
        playerInput_et.isFocused();
        playerInput_et.setText("");
        Log.d(TAG, "newChallenge: " + countChallenge);

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);

    }

    /**
     * -------------------------------------------------------------------------------------------------
     * Choose the right operands based on based on operation symbol,update UI, do calculation ,
     *  and store the correct answer.
     * -------------------------------------------------------------------------------------------------
     */
    private void calculateOperation(){
        switch(operation){
            case '+':
                //operationSymbol_tv.setText("+");
                // set operands to be processed
                firstOperand  = myUtil.randRange_ApiCheck(min, max);
                secondOperand = myUtil.randRange_ApiCheck(min, max);

                // store correct answer
                answerOK = firstOperand + secondOperand;
                break;

            case '-':
                //operationSymbol_tv.setText("-");
                // set operands to be processed
                firstOperand  = myUtil.randRange_ApiCheck(min, max);
                secondOperand = myUtil.randRange_ApiCheck(min, firstOperand);

                // store correct answer
                answerOK = firstOperand - secondOperand;
                break;

            case '*':
                //operationSymbol_tv.setText("*");
                // set operands to be processed
                int guess = myUtil.randRange_ApiCheck(1, 2);
                if (guess == 1){
                    firstOperand  = myUtil.randRange_ApiCheck(multMin, multHMax);
                    secondOperand = myUtil.randRange_ApiCheck(multMin, multLMax);
                }else{
                    firstOperand  = myUtil.randRange_ApiCheck(multMin, multLMax);
                    secondOperand = myUtil.randRange_ApiCheck(multMin, multHMax);
                }

                // store correct answer
                answerOK = firstOperand * secondOperand;
                break;

            case '/':
                //operationSymbol_tv.setText("/");
                // set operands to be processed
                secondOperand = myUtil.randRange_ApiCheck(divMin, divHMax);
                // store correct answer
                answerOK = myUtil.randRange_ApiCheck(divMin, divLMax);
                firstOperand  = answerOK * secondOperand;

                break;
            default:
                break;

        }

        operationSymbol_tv.setText(Character.toString(operation));
        firstOperand_tv.setText(Integer.toString(firstOperand));
        secondOperand_tv.setText(Integer.toString(secondOperand));

    }



    /**
     * -------------------------------------------------------------------------------------------------
     * Show end game message
     * -------------------------------------------------------------------------------------------------
     */
    private void endGame() {
        // reset counter
        countDownIndicator.countdownReset();

        // todo : game over screen
        Toast.makeText(Div_Write_Result_Activity.this, "Congrats! Your score is : " + score + " on " + numChallengeEachLevel, Toast.LENGTH_LONG).show();

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
            // for +, -
            min = max;
            max = 100 * level + 50 * (level - 1);

            // for *,/
            multHMax += 5;
            multLMax += 1;

            divHMax += 2;
            divLMax += 1;

            numChallengeEachLevel += 5;

            // increase time accordingly, but slightly
            timerLength = timerLength + 5000 ;
            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "+max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

    }
}
