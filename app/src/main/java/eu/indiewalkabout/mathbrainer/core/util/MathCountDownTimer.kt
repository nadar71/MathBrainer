package eu.indiewalkabout.mathbrainer.core.util

import android.os.CountDownTimer
import android.util.Log

class MathCountDownTimer : CountDownTimer {

    // internal var timer: CountDownTimer? = null

    lateinit var countdownBar: CountDownIndicator

    var millisInFuture: Long = 0
    var countDownInterval: Long = 0

    constructor(millisInFuture: Long, countDownInterval: Long)
            : super(millisInFuture, countDownInterval) {
        this.millisInFuture = millisInFuture
        this.countDownInterval = countDownInterval
    }


    constructor(millisInFuture: Long, countDownInterval: Long, countdownBar: CountDownIndicator)
            : super(millisInFuture, countDownInterval) {
        this.millisInFuture = millisInFuture
        this.countDownInterval = countDownInterval
        this.countdownBar = countdownBar
    }

    override fun onTick(millisUntilFinished: Long) {
        Log.d(TAG, "onTick: $millisUntilFinished")
        val progress = (millisUntilFinished / 1000).toInt()
        countdownBar.updateCountDownIndicator(progress)
    }

    override fun onFinish() {
        Log.d(TAG, "Time OVER !")
        countdownBar.updateCountDownIndicator(0)

        //feedback to CountDown indicator
        countdownBar.onTimeFinished()
    }


}
