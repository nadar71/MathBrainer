package eu.indiewalkabout.mathbrainer.util;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

public class MathCountDownTimer extends CountDownTimer  {


    private final String TAG = MathCountDownTimer.class.getSimpleName();
    private CountDownIndicator  countdownBar;

    private long        millisInFuture;
    private long        countDownInterval;

    public MathCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.millisInFuture    = millisInFuture;
        this.countDownInterval = countDownInterval;
    }


    public MathCountDownTimer(long millisInFuture, long countDownInterval, CountDownIndicator countdownBar) {
        super(millisInFuture, countDownInterval);
        this.millisInFuture      = millisInFuture;
        this.countDownInterval   = countDownInterval;
        this.countdownBar        = countdownBar;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        Log.d(TAG, "onTick: "+String.valueOf(millisUntilFinished));
        int progress = (int) (millisUntilFinished/1000);
        countdownBar.updateCountDownIndicator(progress);
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "Time OVER !");
        countdownBar.updateCountDownIndicator(0);

        //feedback to CountDown indicator
        countdownBar.onTimeFinished();
    }


    public long getMillisInFuture() {
        return millisInFuture;
    }

    public void setMillisInFuture(long millisInFuture) {
        this.millisInFuture = millisInFuture;
    }

    public long getCountDownInterval() {
        return countDownInterval;
    }

    public void setCountDownInterval(long countDownInterval) {
        this.countDownInterval = countDownInterval;
    }





}
