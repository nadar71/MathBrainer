package eu.indiewalkabout.mathbrainer.util


// import eu.indiewalkabout.mathbrainer.MathCountDownTimer;

import android.content.Context
import android.view.View
import android.widget.ProgressBar

import eu.indiewalkabout.mathbrainer.R

class CountDownIndicator(context: Context, var countdownBar: ProgressBar?,
                         private val caller: IGameFunctions)
    : ProgressBar(context) {

    // this.mathCountDownTimer = new MathCountDownTimer(DEFAULT_MILLISINFUTURE, DEFAULT_COUNTDOWNINTERVAL, this);;
    // TODO : detect configuration changes and update this fields with setCaller to avoid error !!

    // ---------------------------------------------------------------------------------------------
    // Getter and setter stuff
    // ---------------------------------------------------------------------------------------------


    var mathCountDownTimer: MathCountDownTimer? = null
    var thirtyPercentToGo: Int = 0

    /**
     * ---------------------------------------------------------------------------------------------
     * Instantiate, Init and Start the countdown bar
     * @param timerLength               : total timer duration
     * @param timerCountDownInterval    : step update of timer
     * ---------------------------------------------------------------------------------------------
     */
    fun countdownBarStart(timerLength: Long, timerCountDownInterval: Long) {

        // reset counter if any
        this.countdownReset()

        // countdown bar counter
        countdownBar!!.max = timerLength.toInt() / 1000
        thirtyPercentToGo = (timerLength / 1000 * 0.30).toInt()
        countdownBar!!.progress = timerLength.toInt() / 1000
        mathCountDownTimer = MathCountDownTimer(timerLength, timerCountDownInterval, this)
        mathCountDownTimer!!.start()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Reset and destroy countdown bar
     * ---------------------------------------------------------------------------------------------
     */
    fun countdownReset() {
        // countdown bar counter
        if (mathCountDownTimer != null) {
            mathCountDownTimer!!.cancel()
            mathCountDownTimer = null
        }

        countdownBar!!.progressDrawable = resources.getDrawable(R.drawable.horizontal_progress_drawable_green)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Don't include countdown bar in the scene
     * ---------------------------------------------------------------------------------------------
     */
    fun goneCountDownbar() {
        // countdown bar gone
        countdownBar!!.visibility = View.GONE
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Called by MathCountDownTimer onFinish
     * ---------------------------------------------------------------------------------------------
     */
    fun onTimeFinished() {
        // caller.isGameOver();
        caller.checkCountdownExpired()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Called by MathCountDownTimer when a progress bar update is needed
     * ---------------------------------------------------------------------------------------------
     */
    fun updateCountDownIndicator(progress: Int) {
        val currentProgress = countdownBar!!.progress
        if (currentProgress <= thirtyPercentToGo) {
            countdownBar!!.progressDrawable = resources.getDrawable(R.drawable.horizontal_progress_drawable_red)
        }
        caller.updateProgressBar(progress)
    }

    companion object {

        val DEFAULT_MILLISINFUTURE: Long = 30000
        val DEFAULT_COUNTDOWNINTERVAL: Long = 1000
    }


}
