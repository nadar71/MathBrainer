package eu.indiewalkabout.mathbrainer.util;


// import eu.indiewalkabout.mathbrainer.MathCountDownTimer;

import android.content.Context;
import android.graphics.Color;
import android.widget.ProgressBar;

import eu.indiewalkabout.mathbrainer.R;

public class CountDownIndicator extends ProgressBar{

    public static final long DEFAULT_MILLISINFUTURE    = 30000;
    public static final long DEFAULT_COUNTDOWNINTERVAL = 1000;

    private MathCountDownTimer mathCountDownTimer;
    private ProgressBar        countdownBar;
    private IGameFunctions     caller;
    private int                thirtyPercentToGo;


    public CountDownIndicator(Context context, ProgressBar countdownBar, IGameFunctions caller) {
        super(context);
        // this.mathCountDownTimer = new MathCountDownTimer(DEFAULT_MILLISINFUTURE, DEFAULT_COUNTDOWNINTERVAL, this);;
        this.countdownBar       = countdownBar;
        this.caller             = caller;

        // TODO : detect configuration changes and update this fields with setCaller to avoid error !!
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Instantiate, Init and Start the countdown bar
     * @param timerLength               : total timer duration
     * @param timerCountDownInterval    : step update of timer
     * ---------------------------------------------------------------------------------------------
     */
    public void countdownBarStart(long timerLength, long timerCountDownInterval) {

        // reset counter if any
        this.countdownReset();

        // countdown bar counter
        countdownBar.setMax((int)timerLength/1000);
        thirtyPercentToGo = (int) ((timerLength/1000) * 0.30);
        countdownBar.setProgress((int)timerLength/1000);
        mathCountDownTimer = new MathCountDownTimer(timerLength, timerCountDownInterval,this);
        mathCountDownTimer.start();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Reset and destroy countdown bar
     * ---------------------------------------------------------------------------------------------
     */
    public void countdownReset() {
        // countdown bar counter
        if (mathCountDownTimer != null) {
            mathCountDownTimer.cancel();
            mathCountDownTimer = null;
        }

        countdownBar.setProgressDrawable(getResources().getDrawable(R.drawable.horizontal_progress_drawable_green));
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Don't include countdown bar in the scene
     * ---------------------------------------------------------------------------------------------
     */
    public void goneCountDownbar() {
        // countdown bar gone
        countdownBar.setVisibility(GONE);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Called by MathCountDownTimer onFinish
     * ---------------------------------------------------------------------------------------------
     */
    public void onTimeFinished(){
        // caller.isGameOver();
        caller.checkCountdownExpired();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Called by MathCountDownTimer when a progress bar update is needed
     * ---------------------------------------------------------------------------------------------
     */
    public void updateCountDownIndicator(int progress){
        int currentProgress = countdownBar.getProgress();
        if (currentProgress <= thirtyPercentToGo) {
            countdownBar.setProgressDrawable(getResources().getDrawable(R.drawable.horizontal_progress_drawable_red));
        }
        caller.updateProgressBar(progress);

    }

    // ---------------------------------------------------------------------------------------------
    // Getter and setter stuff
    // ---------------------------------------------------------------------------------------------


    public MathCountDownTimer getMathCountDownTimer() {
        return mathCountDownTimer;
    }

    public void setMathCountDownTimer(MathCountDownTimer mathCountDownTimer) {
        this.mathCountDownTimer = mathCountDownTimer;
    }

    public ProgressBar getCountdownBar() {
        return countdownBar;
    }

    public void setCountdownBar(ProgressBar countdownBar) {
        this.countdownBar = countdownBar;
    }


}
