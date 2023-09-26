package eu.indiewalkabout.mathbrainer.util

import android.util.Log
import eu.indiewalkabout.mathbrainer.core.util.CountDownIndicator
import eu.indiewalkabout.mathbrainer.core.util.MathCountDownTimer
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MathCountDownTimerTest {

    @Before
    fun setUp() {
        // mock log
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        /* alternatively :
        PowerMockito.mockStatic(Log::class.java)
        Mockito.`when`(Log.d(any(), any())).then {
            println(it.arguments[1] as String)
            1
        }
         */

    }

    @After
    fun tearDown() {

    }


    // check the countdown start, cancel
    @Test
    fun mathCountDownTimer_dostart_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL
        var mathCountDownTimer = MathCountDownTimer(timerLength, timerCountDownInterval)

        var mathCountDownTimerSpy = Mockito.spy(mathCountDownTimer)
        mathCountDownTimerSpy.start()
        mathCountDownTimerSpy.cancel()
        Mockito.verify(mathCountDownTimerSpy, Mockito.times(1))?.start()
        Mockito.verify(mathCountDownTimerSpy, Mockito.times(1))?.cancel()

    }
}