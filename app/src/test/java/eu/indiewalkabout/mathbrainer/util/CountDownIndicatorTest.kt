package eu.indiewalkabout.mathbrainer.util

import android.widget.ProgressBar
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class CountDownIndicatorTest {
    var countdown: CountDownIndicator? = null
    lateinit var pbar: ProgressBar

    class gameFunc : IGameFunctions {
        override val isGameOver = false
        override fun updateProgressBar(progress: Int) {}
        override fun newChallenge() {}
        override fun checkPlayerInput() {}
        override fun checkCountdownExpired() {}
    }

    var gf: gameFunc? = gameFunc()

    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext


    @Before
    fun setUp() {
        pbar = ProgressBar(appContext)
        countdown = gf?.let { CountDownIndicator(appContext, pbar, it) }
    }

    @After
    fun tearDown() {
        gf = null
        countdown = null
    }


    // check count bar reset
    @Test
    fun CountDownIndicator_countdownReset_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)

        // check method start working on real object
        var countdownSpy = spy(countdown)
        countdownSpy!!.countdownReset()
        verify(countdownSpy, times(1))?.countdownReset()
    }


    // check countbar generation with default value and count down timer start
    @Test
    fun CountDownIndicator_defaultValues_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)

        // default value check
        assertEquals((timerLength.toInt() / 1000), pbar!!.max)
        assertEquals(((timerLength / 1000 * 0.30).toInt()), countdown!!.thirtyPercentToGo)
        assertNotNull(countdown!!.mathCountDownTimer)

        val mthCountdown: MathCountDownTimer = countdown!!.mathCountDownTimer!!
        assertEquals(mthCountdown.countDownInterval, timerCountDownInterval)
        assertEquals(mthCountdown.millisInFuture, timerLength)

        // check method start working on real object
        var mathCountDownTimerSpy = spy(countdown!!.mathCountDownTimer)
        mathCountDownTimerSpy!!.start()
        verify(mathCountDownTimerSpy, times(1))?.start()

    }


    // check countbar generation with 1-1 ratio, ending immediately: simulate finish call
    @Test
    fun CountDownIndicator_unaryValues_ending() {
        var timerLength: Long = 1000
        var timerCountDownInterval: Long = 1000

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)


        assertEquals((timerLength.toInt() / 1000), pbar!!.max)
        assertEquals(((timerLength / 1000 * 0.30).toInt()), countdown!!.thirtyPercentToGo)
        assertNotNull(countdown!!.mathCountDownTimer)

        val mthCountdown: MathCountDownTimer = countdown!!.mathCountDownTimer!!
        assertEquals(mthCountdown.countDownInterval, timerCountDownInterval)
        assertEquals(mthCountdown.millisInFuture, timerLength)

        // check method finish working on real object
        var mathCountDownTimerSpy = spy(countdown!!.mathCountDownTimer)
        mathCountDownTimerSpy!!.onFinish()
        verify(mathCountDownTimerSpy, times(1))?.onFinish()
    }


    // check onTicks calling working on standard value
    @Test
    fun CountDownIndicator_tickexecuted_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)

        // check method start working on real object
        var mathCountDownTimerSpy = spy(countdown!!.mathCountDownTimer)
        mathCountDownTimerSpy!!.onTick(1000)
        verify(mathCountDownTimerSpy, times(1))?.onTick(1000)
    }


    // check updateCountDownIndicator
    @Test
    fun CountDownIndicator_updateCountDownIndicator_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)

        // check method start working on real object
        var countdownSpy = spy(countdown)
        countdownSpy!!.updateCountDownIndicator(100)
        verify(countdownSpy, times(1))?.updateCountDownIndicator(100)
    }


    // check onTimeFinished
    @Test
    fun CountDownIndicator_onTimeFinished_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)

        // check method start working on real object
        var countdownSpy = spy(countdown)
        countdownSpy!!.updateCountDownIndicator(100)
        verify(countdownSpy, times(1))?.updateCountDownIndicator(100)
    }


    // check updateCountDownIndicator on progress bar lower then 30%
    @Test
    fun CountDownIndicator_updateLessthirtyPercentToGo_correct() {

        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

        countdown!!.countdownBarStart(timerLength, timerCountDownInterval)
        countdown!!.countdownBar!!.progress = (countdown!!.thirtyPercentToGo * 0.5).toInt()

        assertTrue(countdown!!.countdownBar!!.progress <= countdown!!.thirtyPercentToGo)

        // check method start working on real object
        var countdownSpy = spy(countdown)
        var prog = countdown!!.countdownBar!!.progress
        countdownSpy!!.updateCountDownIndicator(prog)
        verify(countdownSpy, times(1))?.updateCountDownIndicator(prog)
    }

}