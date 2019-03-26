package eu.indiewalkabout.mathbrainer.aritmetic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
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

import eu.indiewalkabout.mathbrainer.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator;
import eu.indiewalkabout.mathbrainer.util.GameOverDialog;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.MyKeyboard;
import eu.indiewalkabout.mathbrainer.util.myUtil;



public class Math_Op_Write_Result_Activity extends AppCompatActivity implements IGameFunctions {

    // admob banner ref
    private AdView mAdView;

    // tag for log
    private final static String TAG = Math_Op_Write_Result_Activity.class.getSimpleName();

    // view ref
    private TextView numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;
    private TextView firstOperand_tv, secondOperand_tv, operationSymbol_tv, result_tv;
    private ArrayList<ImageView> lifesValue_iv ;
    private EditText playerInput_et;

    // store initial text color
    private ColorStateList quizDefaultTextColor;


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

    private char[] symbols = null; // = {'+','-','*','/'};

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

    // custom keyboard instance
    MyKeyboard keyboard;

    // game over dialog
    GameOverDialog gameOverDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_op_write_result);

        // Check if it's mixed op or single specific operation
        setOperationSymbol();


        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle
        // the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(Math_Op_Write_Result_Activity.this));


        // set views ref
        firstOperand_tv    = (TextView)  findViewById(R.id.firstOperand_tv);
        secondOperand_tv   = (TextView)  findViewById(R.id.secondOperand_tv);
        operationSymbol_tv = (TextView)  findViewById(R.id.operationSymbol_tv);

        // show result tv
        result_tv = findViewById(R.id.result_tv);

        // store quiz text color for later use
        quizDefaultTextColor = firstOperand_tv.getTextColors();

        scoreValue_tv      = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv      = (TextView)  findViewById(R.id.levelValue_tv);
        playerInput_et     = (EditText)  findViewById(R.id.playerInput_et);

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
        countDownIndicator = new CountDownIndicator(Math_Op_Write_Result_Activity.this,
                (ProgressBar) countdownBar, Math_Op_Write_Result_Activity.this);

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

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check and set the symbol of the operation from the caller intent
     * ---------------------------------------------------------------------------------------------
     */
    private void setOperationSymbol() {
        Intent intent = getIntent();
        char[] operationSpec;
        if (intent.hasExtra(ChooseGameActivity.OPERATION_KEY)) {
            operationSpec =  intent.getStringExtra(ChooseGameActivity.OPERATION_KEY).toCharArray();
            switch(operationSpec[0]){
                case '+':
                    symbols = new char[1];
                    symbols[0] = '+';
                    break;

                case '-':
                    symbols = new char[1];
                    symbols[0] = '-';
                    break;

                case '*':
                    symbols = new char[1];
                    symbols[0] = '*';
                    break;

                case '/':
                    symbols = new char[1];
                    symbols[0] = '/';
                    break;
                default:
                    break;

            }
        } else {
            symbols = new char[4];
            symbols[0] = '+';
            symbols[1] = '-';
            symbols[2] = '*';
            symbols[3] = '/';
        }
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
     * Create  and setup customkeyboard
     * -------------------------------------------------------------------------------------------------
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
        keyboard.setInputConnection(ic, Math_Op_Write_Result_Activity.this);
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

        // stop timer
        countDownIndicator.countdownReset();

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return;
        }

        inputNum = Integer.parseInt(tmp);

        Log.d(TAG, "checkPlayerInput: inputNum : " + inputNum);

        // check if result is ok...
        if (inputNum != 0  && inputNum == answerOK) {

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

            if (gameover == false) {
                // show result and start a new game session if allowed
                showResult(false);
            }

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
            // show result and start a new game session if allowed
            showResult(false);
        }

    }



    /**
     * -------------------------------------------------------------------------------------------------
     * Show the result of the
     * -------------------------------------------------------------------------------------------------
     */
    private void showResult(boolean win) {
        result_tv.setVisibility(View.VISIBLE);
        if (win == true) {
            result_tv.setText(getResources().getString(R.string.ok_str));
            result_tv.setTextColor(Color.GREEN);
            firstOperand_tv.setTextColor(Color.GREEN);
            secondOperand_tv.setTextColor(Color.GREEN);
            operationSymbol_tv.setTextColor(Color.GREEN);
            playerInput_et.setTextColor(Color.GREEN);
            // hide keyboard
            keyboard.setVisibility(View.INVISIBLE);
            newchallengeAfterTimerLength(1000);


        }else{
            result_tv.setText(getResources().getString(R.string.wrong_str) + " : " + answerOK);
            result_tv.setTextColor(Color.RED);
            firstOperand_tv.setTextColor(Color.RED);
            secondOperand_tv.setTextColor(Color.RED);
            operationSymbol_tv.setTextColor(Color.RED);
            playerInput_et.setTextColor(Color.RED);
            // hide keyboard
            keyboard.setVisibility(View.INVISIBLE);
            newchallengeAfterTimerLength(1000);

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
                result_tv.setVisibility(View.INVISIBLE);
                firstOperand_tv.setTextColor(quizDefaultTextColor);
                secondOperand_tv.setTextColor(quizDefaultTextColor);
                operationSymbol_tv.setTextColor(quizDefaultTextColor);
                playerInput_et.setTextColor(quizDefaultTextColor);
                keyboard.setVisibility(View.VISIBLE);
                newChallenge();
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
        // update life counts
        lifes--;

        Log.d(TAG, "isGameOver: " + lifes);

        // Update UI
        if ( lifes > -1) { lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE); }

        // check game over condition
        if ( lifes <= 0){
            // hide input field
            playerInput_et.setVisibility(View.INVISIBLE);

            // hide keyboard
            keyboard.setVisibility(View.INVISIBLE);

            endGame();
            return true;

        }else {
            // lifes remaining >0, restart a new counter
            // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
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
    @Override
    public void newChallenge() {
        // set operation to be processed; general case symbols.length-1 > 1
        operation = symbols[myUtil.randRange_ApiCheck(0, symbols.length - 1)];

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

        gameOverDialog = new GameOverDialog(this,
                Math_Op_Write_Result_Activity.this, this);

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
