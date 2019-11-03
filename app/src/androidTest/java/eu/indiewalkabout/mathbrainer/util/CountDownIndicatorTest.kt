package eu.indiewalkabout.mathbrainer.util

import org.junit.Assert.*

import android.content.Context
import android.widget.ProgressBar
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CountDownIndicatorAndroidTest {
    var  countdown : CountDownIndicator? = null
    lateinit var pbar: ProgressBar

    class  gameFunc  : IGameFunctions{
        override val isGameOver = false
        override fun updateProgressBar(progress: Int) {}
        override fun newChallenge() {}
        override fun checkPlayerInput() {}
        override fun checkCountdownExpired() {}
    }
    var gf : gameFunc? = gameFunc()

    // val appContext : Context = ApplicationProvider.getApplicationContext<Context>()

    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        pbar = ProgressBar(appContext)
        countdown = gf?.let { CountDownIndicator(appContext,pbar, it) }
    }

    @After
    fun tearDown() {
        gf = null
        countdown = null
    }

    @Test
    fun countdownBarStart_defaultValues_correct() {
        var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
        var timerCountDownInterval = CountDownIndicator.DEFAULT_MILLISINFUTURE


        countdown!!.countdownBarStart(CountDownIndicator.DEFAULT_MILLISINFUTURE, CountDownIndicator.DEFAULT_MILLISINFUTURE)

        assertEquals((timerLength.toInt() / 1000), pbar!!.max )
        // assertEquals(((timerLength / 1000 * 0.30).toInt()), countdown!!.thirtyPercentToGo )
        // assertNotNull(countdown!!.mathCountDownTimer)
        //
        // val mthCountdown : MathCountDownTimer  = countdown!!.mathCountDownTimer!!
        // assertEquals(mthCountdown.countDownInterval, timerLength)
        // assertEquals(mthCountdown.millisInFuture, timerCountDownInterval)


    }
/*


    @Test
    fun getMathCountDownTimer() {

    }

    @Test
    fun setMathCountDownTimer() {
    }



    @Test
    fun countdownReset() {
    }

    @Test
    fun goneCountDownbar() {
    }

    @Test
    fun onTimeFinished() {
    }

    @Test
    fun updateCountDownIndicator() {
    }

    @Test
    fun getCountdownBar() {
    }

    @Test
    fun setCountdownBar() {
    }

 */
}