package eu.indiewalkabout.mathbrainer.aritmetic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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



public class MixedOp_Choose_Result_Activity extends AppCompatActivity implements IGameFunctions {

    // admob banner ref
    private AdView mAdView;

    // tag for log
    private final static String TAG = MixedOp_Choose_Result_Activity.class.getSimpleName();

    // view ref
    private TextView scoreValue_tv, levelValue_tv;
    private TextView firstOperand_tv, secondOperand_tv, operationSymbol_tv;
    private ArrayList<ImageView> lifesValue_iv ;


    Button answer01Btn, answer02Btn, answer03Btn, answer04Btn, answer05Btn, answer06Btn, answer07Btn, answer08Btn, answer09Btn;


    // numbers to be processed
    private int  firstOperand, secondOperand;
    private char operation;

    // answer and its stuff
    private int answerOK;
    private int correctBtnNumber = 1;
    private int offset           = 10;
    private int pressedBtnValue  = 0;

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

    // random range for answer btn number
    // changing while level growing
    private int minAnswerBtnNum              = 3;
    private int maxAnswerBtnNum              = 9;
    private int currentLevelAnswerBtnVisible = 3;
    private int levelAnswerBtnTotalNum       = 3;

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
        setContentView(R.layout.activity_mixedop_choose_result);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MixedOp_Choose_Result_Activity.this));

        // set views ref
        firstOperand_tv    = (TextView)  findViewById(R.id.firstOperand_tv);
        secondOperand_tv   = (TextView)  findViewById(R.id.secondOperand_tv);
        operationSymbol_tv = (TextView)  findViewById(R.id.operationSymbol_tv);

        scoreValue_tv      = (TextView)  findViewById(R.id.scoreValue_tv);
        levelValue_tv      = (TextView)  findViewById(R.id.levelValue_tv);

        // init lifes led images
        lifesValue_iv = new ArrayList<ImageView>();
        lifesValue_iv.add((ImageView) findViewById(R.id.life_01_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_02_iv));
        lifesValue_iv.add((ImageView) findViewById(R.id.life_03_iv));

        // btn references
        answer01Btn =  findViewById(R.id.answer_plus_btn);
        answer02Btn =  findViewById(R.id.answer_minus_btn);
        answer03Btn =  findViewById(R.id.answer_mult_btn);
        answer04Btn =  findViewById(R.id.answer_div_btn);
        answer05Btn =  findViewById(R.id.answer_05Btn);
        answer06Btn =  findViewById(R.id.answer_06Btn);
        answer07Btn =  findViewById(R.id.answer_07Btn);
        answer08Btn =  findViewById(R.id.answer_08Btn);
        answer09Btn =  findViewById(R.id.answer_09Btn);

        // define wrong answers storage
        wrongAnswer = new ArrayList<>();

        // countdown ref
        countdownBar = (ProgressBar)findViewById(R.id.progressbar);

        // Create new count down indicator, without starting it
        countDownIndicator = new CountDownIndicator(MixedOp_Choose_Result_Activity.this,
                (ProgressBar) countdownBar, MixedOp_Choose_Result_Activity.this);

        // start with first challenge and countdown init
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


        answer05Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer06Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer07Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer08Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
            }
        });


        answer09Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                pressedBtnValue = Integer.parseInt((String)b.getText());
                checkPlayerInput();
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
            Toast.makeText(MixedOp_Choose_Result_Activity.this, "OK!", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(MixedOp_Choose_Result_Activity.this, "WRONG...", Toast.LENGTH_SHORT).show();

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
        // clear wrong answers list
        wrongAnswer.clear();

        // reset the number of visible button
        currentLevelAnswerBtnVisible = levelAnswerBtnTotalNum;

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
                answerOK = firstOperand + secondOperand;
                break;

            case '-':
                // set operands to be processed
                firstOperand  = myUtil.randRange_ApiCheck(min, max);
                secondOperand = myUtil.randRange_ApiCheck(min, firstOperand);

                // store correct answer
                answerOK = firstOperand - secondOperand;
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
                answerOK = firstOperand * secondOperand;
                break;

            case '/':
                // set operands to be processed
                secondOperand = myUtil.randRange_ApiCheck(divMin, divHMax);
                // store correct answer
                answerOK = myUtil.randRange_ApiCheck(divMin, divLMax);
                firstOperand  = answerOK * secondOperand;

                break;
            default:
                break;

        }

        // set operations value in view
        operationSymbol_tv.setText(Character.toString(operation));
        firstOperand_tv.setText(Integer.toString(firstOperand));
        secondOperand_tv.setText(Integer.toString(secondOperand));

        // setup answers on button
        setupAnswersBtn();


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create setup correct answer and false answer on buttons
     * ---------------------------------------------------------------------------------------------
     */
    private void setupAnswersBtn() {
        // choose the button where put the correct answer
        correctBtnNumber = myUtil.randRange_ApiCheck(minAnswerBtnNum, maxAnswerBtnNum);
        Button tmpBtn    = getTheBtnNumber(correctBtnNumber);
        tmpBtn.setText(Integer.toString(answerOK));

        // visible answer button update
        currentLevelAnswerBtnVisible--;

        // set wrong answer on the others
        for(int i = 1; i <= maxAnswerBtnNum; i++){
            if (i != correctBtnNumber){
                switch(operation){

                    case '+':
                    case '-': {
                        tmpBtn = getTheBtnNumber(i);
                        int result = 0;
                        do {
                            result = randomOffsetSum();
                        } while (wrongAnswer.lastIndexOf(result) > 0);
                        wrongAnswer.add(result);

                        tmpBtn.setText(String.valueOf(result));

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn);

                        break;
                    }

                    case '*': {
                        tmpBtn = getTheBtnNumber(i);
                        int result = 0;
                        do {
                            result = randomOffsetMult();
                        } while (wrongAnswer.lastIndexOf(result) > 0);
                        wrongAnswer.add(result);

                        tmpBtn.setText(String.valueOf(result));

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn);

                        break;
                    }

                    case '/': {
                        tmpBtn = getTheBtnNumber(i);
                        int result = 0;
                        do {
                            result = randomOffsetSum();
                        } while (wrongAnswer.lastIndexOf(result) > 0);
                        wrongAnswer.add(result);

                        tmpBtn.setText(String.valueOf(result));

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn);

                        break;
                    }
                    default:
                        break;

                }
            }else { // the btn with the right answer must be alwys visible
                tmpBtn = getTheBtnNumber(correctBtnNumber);
                tmpBtn.setVisibility(View.VISIBLE);
            }
        }
    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Set btn visible or not depending on current level button visible
     * @param thisBtn
     * ---------------------------------------------------------------------------------------------
     */
    private void setAnswerBtnVisibility(Button thisBtn) {

        int guess = myUtil.randomSignChooser();
        if ( (guess > 0 ) && (currentLevelAnswerBtnVisible > 0)  ) {
            thisBtn.setVisibility(View.VISIBLE);
            currentLevelAnswerBtnVisible--;
        }else{
            thisBtn.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    private int randomOffsetSum(){
        int result = myUtil.randRange_ApiCheck(1, (int)(offset * 1.5));
        if ( (result >= 1) && (result <= 3) ) {
            return answerOK + myUtil.randomSignChooser()* result;
        }
        return answerOK + result;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for multiplication generator
     * ---------------------------------------------------------------------------------------------
     */
    private int randomOffsetMult(){
        int result = myUtil.randRange_ApiCheck(1, offset * 2);
        int sign   = myUtil.randomSignChooser();

        if ( (result >= 4) && (result <= 11) ){
            if (sign > 0 ) {
                return answerOK + myUtil.randomSignChooser() * result;
            } else {
                return (int) ( answerOK + ( myUtil.randomSignChooser() * (int) ( 10 + result) * 0.1));
            }

        } else if ((result > 11) && (result <= 16)) {
            if (sign > 0 ) {
                return answerOK + myUtil.randomSignChooser() * result;
            } else {
                return (int) answerOK * (int)(result * 0.1);
            }

        } else if (result > 16)  {
            if (sign > 0 ) {
                return answerOK + myUtil.randomSignChooser() * result;
            } else {
                return  (int) ( answerOK + ( myUtil.randomSignChooser() * (int) ( 3 + result) * 0.1));
            }

        } else
            return answerOK + myUtil.randomSignChooser() * result;

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
            case 5 : return answer05Btn;
            case 6 : return answer06Btn;
            case 7 : return answer07Btn;
            case 8 : return answer08Btn;
            case 9 : return answer09Btn;
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
        // reset counter
        countDownIndicator.countdownReset();

        // todo : game over screen
        Toast.makeText(MixedOp_Choose_Result_Activity.this, "Congrats! Your score is : " + score + " on " + numChallengeEachLevel, Toast.LENGTH_LONG).show();

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

            // increase the number of visible answer button
            if (level < 9 ) levelAnswerBtnTotalNum++;

            // increase time accordingly, but slightly
            timerLength = timerLength + 5000 ;
            Log.d(TAG, "updatingLevel: New Level! new min : "+min+" new max: "+max+" new level : "+level+" Timer now at : " + (timerLength/1000) + " sec.");
        }

    }

}
