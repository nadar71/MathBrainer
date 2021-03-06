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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.ui.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.statistics.Results;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator;
import eu.indiewalkabout.mathbrainer.util.GameOverDialog;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility;
import eu.indiewalkabout.mathbrainer.util.MyKeyboard;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;



public class Math_Op_Write_Result_Activity extends AppCompatActivity implements IGameFunctions {

    final private UnityAdsListener unityAdsListener = new UnityAdsListener();

    // admob banner ref
    private AdView mAdView;

    private final static String TAG = Math_Op_Write_Result_Activity.class.getSimpleName();

    // view ref
    private TextView numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;
    private TextView firstOperand_tv, secondOperand_tv, operationSymbol_tv, result_tv,scoreLabel_tv,
            highscore_label_tv,highscore_value_tv;
    private ArrayList<ImageView> lifesValue_iv ;
    private EditText playerInput_et;
    private ImageView backhome_img;

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
    private String   scoreType;
    private String[] scoreTypeList = {"sum_write_result_game_score",
                                      "diff_write_result_game_score",
                                      "mult_write_result_game_score",
                                      "div_write_result_game_score",
                                      "mix_write_result_game_score"};

    // num of challenge to pass to next level
    // changing while level growing
    private int numChallengeEachLevel =  12;
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

        // Unity ads init
        UnityAds.initialize(this,getResources().getString(R.string.unityads_key),unityAdsListener);

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
        backhome_img       = (ImageView)  findViewById(R.id.backhome_img);

        scoreLabel_tv          = (TextView) findViewById(R.id.scoreLabel_tv);
        highscore_label_tv     = (TextView) findViewById(R.id.high_scoreLabel_tv);
        highscore_value_tv     = (TextView) findViewById(R.id.high_scoreValue_tv);

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

        backhome_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // saves score
                isComingHome();

                // show unityads randomic
                MathBrainerUtility.showUnityAdsRandom(Math_Op_Write_Result_Activity.this);

                Intent intent = new Intent(Math_Op_Write_Result_Activity.this, ChooseGameActivity.class);
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
                    scoreType = scoreTypeList[0];
                    break;

                case '-':
                    symbols = new char[1];
                    symbols[0] = '-';
                    scoreType = scoreTypeList[1];
                    break;

                case '*':
                    symbols = new char[1];
                    symbols[0] = '*';
                    scoreType = scoreTypeList[2];
                    break;

                case '/':
                    symbols = new char[1];
                    symbols[0] = '/';
                    scoreType = scoreTypeList[3];
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
            scoreType = scoreTypeList[4];
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
        keyboard.setInputConnection(ic, Math_Op_Write_Result_Activity.this);
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

        // stop timer
        countDownIndicator.countdownReset();

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return;
        }

        inputNum = Integer.parseInt(tmp);

        Log.d(TAG, "checkPlayerInput: inputNum : " + inputNum);

        // check if result is ok...
        // if (inputNum != 0  && inputNum == answerOK) {
        if (inputNum == answerOK) {
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
     * Show the result of the game session
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
        firstOperand_tv.setTextColor(Color.GREEN);
        secondOperand_tv.setTextColor(Color.GREEN);
        operationSymbol_tv.setTextColor(Color.GREEN);
        playerInput_et.setTextColor(Color.GREEN);
        // hide keyboard
        keyboard.setVisibility(View.INVISIBLE);

        // statistics
        Results.incrementGameResultsThread("operations_executed");
        Results.incrementGameResultsThread("operations_ok");
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show wrong in case of game lose
     * ---------------------------------------------------------------------------------------------
     */
    private void showWrongResult() {
        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.wrong_str) + " : " + answerOK);
        result_tv.setTextColor(Color.RED);
        firstOperand_tv.setTextColor(Color.RED);
        secondOperand_tv.setTextColor(Color.RED);
        operationSymbol_tv.setTextColor(Color.RED);
        playerInput_et.setTextColor(Color.RED);
        // hide keyboard
        keyboard.setVisibility(View.INVISIBLE);

        // statistics
        Results.incrementGameResultsThread("operations_executed");
        Results.incrementGameResultsThread("operations_ko");
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
        firstOperand_tv.setTextColor(quizDefaultTextColor);
        secondOperand_tv.setTextColor(quizDefaultTextColor);
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
            Results.updateGameResultHighscoreThread(scoreType, score);
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
        Results.updateGameResultHighscoreThread(scoreType, score);
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
        // set operation to be processed; general case symbols.length-1 > 1
        operation = symbols[MathBrainerUtility.randRange_ApiCheck(0, symbols.length - 1)];

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
     * ---------------------------------------------------------------------------------------------
     * Choose the right operands based on based on operation symbol,update UI, do calculation ,
     *  and store the correct answer.
     * ---------------------------------------------------------------------------------------------
     */
    private void calculateOperation(){
        switch(operation){
            case '+':
                //operationSymbol_tv.setText("+");
                // set operands to be processed
                firstOperand  = MathBrainerUtility.randRange_ApiCheck(min, max);
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, max);

                // store correct answer
                answerOK = firstOperand + secondOperand;

                // statistics
                Results.incrementGameResultsThread("sums");

                operationSymbol_tv.setText(Character.toString(operation));

                break;

            case '-':
                //operationSymbol_tv.setText("-");
                // set operands to be processed
                firstOperand  = MathBrainerUtility.randRange_ApiCheck(min, max);
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, firstOperand);

                // store correct answer
                answerOK = firstOperand - secondOperand;

                operationSymbol_tv.setText(Character.toString(operation));

                // statistics
                Results.incrementGameResultsThread("differences");

                break;

            case '*':
                //operationSymbol_tv.setText("*");
                // set operands to be processed
                int guess = MathBrainerUtility.randRange_ApiCheck(1, 2);
                if (guess == 1){
                    firstOperand  = MathBrainerUtility.randRange_ApiCheck(multMin, multHMax);
                    secondOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multLMax);
                }else{
                    firstOperand  = MathBrainerUtility.randRange_ApiCheck(multMin, multLMax);
                    secondOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multHMax);
                }

                // store correct answer
                answerOK = firstOperand * secondOperand;

                operationSymbol_tv.setText("X");

                // statistics
                Results.incrementGameResultsThread("multiplications");

                break;

            case '/':
                //operationSymbol_tv.setText("/");
                // set operands to be processed
                secondOperand = MathBrainerUtility.randRange_ApiCheck(divMin, divHMax);
                // store correct answer
                answerOK = MathBrainerUtility.randRange_ApiCheck(divMin, divLMax);
                firstOperand  = answerOK * secondOperand;

                operationSymbol_tv.setText(Character.toString(operation));

                // statistics
                Results.incrementGameResultsThread("divisions");

                break;
            default:
                break;

        }


        firstOperand_tv.setText(Integer.toString(firstOperand));
        secondOperand_tv.setText(Integer.toString(secondOperand));

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
                Math_Op_Write_Result_Activity.this, this);

        hideLastQuiz();

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Hide quiz
     * ---------------------------------------------------------------------------------------------
     */
    private void hideLastQuiz() {
        playerInput_et.setVisibility(View.INVISIBLE);
        firstOperand_tv.setVisibility(View.INVISIBLE);
        secondOperand_tv.setVisibility(View.INVISIBLE);
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
        MathBrainerUtility.showUnityAdsRandom(this);

    }

}
