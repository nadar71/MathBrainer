package eu.indiewalkabout.mathbrainer.aritmetic;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import eu.indiewalkabout.mathbrainer.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator;
import eu.indiewalkabout.mathbrainer.util.EndGameSessionDialog;
import eu.indiewalkabout.mathbrainer.util.GameOverDialog;
import eu.indiewalkabout.mathbrainer.util.IGameFunctions;
import eu.indiewalkabout.mathbrainer.util.myUtil;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

public class RandomOperationActivity extends AppCompatActivity implements IGameFunctions {

    final private UnityAdsListener unityAdsListener = new UnityAdsListener();

    // admob banner ref
    private AdView mAdView;

    // tag for log
    private final static String TAG = Math_Op_Choose_Result_Activity.class.getSimpleName();

    // view ref
    private TextView numberToBeDoubled_tv, scoreValue_tv, levelValue_tv;

    private TextView firstOperand_tv, secondOperand_tv, operationSymbol_tv,
            operation_result_tv,result_tv, equals_sign_tv, instruction_tv;

    private ImageView backhome_img;

    private ArrayList<ImageView> lifesValue_iv ;

    private GridLayout gridLayout;

    // store initial text color
    private ColorStateList quizDefaultTextColor;

    Button answer_plus_Btn, answer_minus_Btn, answer_mult_Btn, answer_div_Btn;

    // numbers to be processed
    private int  firstOperand, secondOperand;
    private char operation;

    // answer and its stuff
    private int    answer;
    private String operationOK;
    private int correctBtnNumber    = 1;
    private int offset              = 10;
    private String pressedBtnValue  = "";

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



    // store wring answer to avoid duplicates
    ArrayList<Integer> wrongAnswer;

    // operation symbols
    private char[] symbols = {'+','-','*','/'};

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

    // game session end dialog
    EndGameSessionDialog endSessiondialog;

    // game over dialog
    GameOverDialog gameOverDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_operation);

        // Unity ads init
        UnityAds.initialize(this,getResources().getString(R.string.unityads_key),unityAdsListener);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(RandomOperationActivity.this));

        // set views ref
        firstOperand_tv     = (TextView)   findViewById(R.id.firstOperand_tv);
        secondOperand_tv    = (TextView)   findViewById(R.id.secondOperand_tv);
        operationSymbol_tv  = (TextView)   findViewById(R.id.operationSymbol_tv);
        operation_result_tv = (TextView)   findViewById(R.id.operation_result_tv);
        equals_sign_tv      = (TextView)   findViewById(R.id.equalLabel_tv);
        instruction_tv      = (TextView)   findViewById(R.id.instruction_tv);
        gridLayout          = (GridLayout) findViewById(R.id.answerBtnGrid);
        backhome_img       = (ImageView)  findViewById(R.id.backhome_img);

        // show result tv
        result_tv = findViewById(R.id.result_tv);

        // store quiz text color for later use
        quizDefaultTextColor = firstOperand_tv.getTextColors();

        scoreValue_tv      = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv      = (TextView)  findViewById(R.id.levelValue_tv);

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // btn references
        answer_plus_Btn =  findViewById(R.id.answer_plus_btn);
        answer_minus_Btn =  findViewById(R.id.answer_minus_btn);
        answer_mult_Btn =  findViewById(R.id.answer_mult_btn);
        answer_div_Btn =  findViewById(R.id.answer_div_btn);


        // define wrong answers storage
        wrongAnswer = new ArrayList<>();

        // countdown ref
        countdownBar = (ProgressBar)findViewById(R.id.progressbar);

        // Create new count down indicator, without starting it
        countDownIndicator = new CountDownIndicator(RandomOperationActivity.this,
                (ProgressBar) countdownBar, RandomOperationActivity.this);

        // start with first challenge and countdown init
        newChallenge();

        // activate clicks on answer buttons
        setBtnPressedListener();

        // set first level
        updateLevel();

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();

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
     * Set up the button pressed listener and checking answers
     * ---------------------------------------------------------------------------------------------
     */
    private void setBtnPressedListener(){
        answer_plus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = "+";
                checkPlayerInput();
            }
        });


        answer_minus_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = "-";
                checkPlayerInput();
            }
        });


        answer_mult_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = "*";
                checkPlayerInput();
            }
        });


        answer_div_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = "/";
                checkPlayerInput();
            }
        });

        backhome_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show unityads randomic
                myUtil.showUnityAdsRandom(RandomOperationActivity.this);

                Intent intent = new Intent(RandomOperationActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void checkPlayerInput() {

        Log.d(TAG, "checkPlayerInput: pressedBtnValue : " + pressedBtnValue);

        // check if result is ok...
        if ( !pressedBtnValue.isEmpty()  && pressedBtnValue.equals(operationOK)) {

            updateScore();

            countChallenge++;

            setOperationText();

            operationSymbol_tv.setVisibility(View.VISIBLE);

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
     * Set the currect symbol operation for multiplication: X for *
     * ---------------------------------------------------------------------------------------------
     */
    private void setOperationText(){
        if (operationOK.equals("*")) {
            operationSymbol_tv.setText("X");
        } else {
            operationSymbol_tv.setText(operationOK);
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
        instruction_tv.setVisibility(View.INVISIBLE);

        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.ok_str));
        result_tv.setTextColor(Color.GREEN);
        firstOperand_tv.setTextColor(Color.GREEN);
        secondOperand_tv.setTextColor(Color.GREEN);

        setOperationText();
        // operationSymbol_tv.setText(operationOK);
        operationSymbol_tv.setTextColor(Color.GREEN);
        operation_result_tv.setTextColor(Color.GREEN);
        equals_sign_tv.setTextColor(Color.GREEN);
        gridLayout.setVisibility(View.INVISIBLE);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show wrong in case of game lose
     * ---------------------------------------------------------------------------------------------
     */
    private void showWrongResult() {
        instruction_tv.setVisibility(View.INVISIBLE);

        result_tv.setVisibility(View.VISIBLE);
        result_tv.setText(getResources().getString(R.string.wrong_str) + " : " + operationOK);
        result_tv.setTextColor(Color.RED);
        firstOperand_tv.setTextColor(Color.RED);
        secondOperand_tv.setTextColor(Color.RED);

        setOperationText();
        // operationSymbol_tv.setText(operationOK);
        operationSymbol_tv.setTextColor(Color.RED);
        operation_result_tv.setTextColor(Color.RED);
        equals_sign_tv.setTextColor(Color.RED);
        gridLayout.setVisibility(View.INVISIBLE);
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
        instruction_tv.setVisibility(View.VISIBLE);
        firstOperand_tv.setTextColor(quizDefaultTextColor);
        secondOperand_tv.setTextColor(quizDefaultTextColor);
        operationSymbol_tv.setTextColor(quizDefaultTextColor);
        operation_result_tv.setTextColor(quizDefaultTextColor);
        equals_sign_tv.setTextColor(quizDefaultTextColor);
        gridLayout.setVisibility(View.VISIBLE);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Update score view
     * ---------------------------------------------------------------------------------------------
     */
    private void updateScore() {
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

        Log.d(TAG, "isGameOver: " + lifes);

        // Update UI
        if ( lifes > -1) { lifesValue_iv.get(lifes).setVisibility(View.INVISIBLE);}

        // check game over condition
        if ( lifes <= 0){
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
        // clear wrong answers list
        wrongAnswer.clear();

        // reset the number of visible button
        // todo :delete
        // currentLevelAnswerBtnVisible = levelAnswerBtnTotalNum;

        // set operation to be processed
        operation    = symbols[myUtil.randRange_ApiCheck(0, symbols.length-1)];

        // calculate the quiz operation
        calculateOperation();

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
                // set operands to be processed
                firstOperand  = myUtil.randRange_ApiCheck(min, max);
                secondOperand = myUtil.randRange_ApiCheck(min, max);

                // store correct answer
                answer      = firstOperand + secondOperand;
                break;

            case '-':
                // set operands to be processed
                firstOperand  = myUtil.randRange_ApiCheck(min, max);
                secondOperand = myUtil.randRange_ApiCheck(min, firstOperand);

                // store correct answer
                answer = firstOperand - secondOperand;
                break;

            case '*':
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
                answer = firstOperand * secondOperand;
                break;

            case '/':
                // set operands to be processed
                secondOperand = myUtil.randRange_ApiCheck(divMin, divHMax);
                // store correct answer
                answer = myUtil.randRange_ApiCheck(divMin, divLMax);
                firstOperand  = answer * secondOperand;

                break;
            default:
                break;

        }

        // right answer
        operationOK = Character.toString(operation);

        // set values in view
        operation_result_tv.setText(Integer.toString(answer));

        // hide correct operation
        // operationSymbol_tv.setText(operationOK);
        // operationSymbol_tv.setVisibility(View.INVISIBLE);
        operationSymbol_tv.setText(getResources().getString(R.string.question_mark));

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

        // reset counter
        countDownIndicator.countdownReset();

        showWrongResult();

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
                RandomOperationActivity.this, this);


        // hide input field
        firstOperand_tv.setVisibility(View.INVISIBLE);
        secondOperand_tv.setVisibility(View.INVISIBLE);
        operationSymbol_tv.setVisibility(View.INVISIBLE);
        operation_result_tv.setVisibility(View.INVISIBLE);
        equals_sign_tv.setVisibility(View.INVISIBLE);
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

            // increase the number of visible answer button
            // if (level < 9 ) levelAnswerBtnTotalNum++;

            // increase time accordingly, but slightly
            timerLength = timerLength + 5000 ;
            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "+max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

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

        // show unityads randomic
        myUtil.showUnityAdsRandom(this);

    }
}
