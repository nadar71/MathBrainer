package eu.indiewalkabout.mathbrainer.games.arithmetic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// TODO
@LargeTest
@RunWith(AndroidJUnit4::class)
class DoubleNumberActivityTest {

    lateinit var doubleNumberActivity: eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity

    @get:Rule
    val activityRule = ActivityTestRule(eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity::class.java)

    @Before
    fun setUp() {
        doubleNumberActivity = activityRule.activity
    }

    @After
    fun tearDown() {
    }

    /*
    @Test
    fun `getCountDownIndicator$app_debug`() {

    }

    @Test
    fun `setCountDownIndicator$app_debug`() {
    }

    @Test
    fun `getEndSessiondialog$app_debug`() {
    }

    @Test
    fun `setEndSessiondialog$app_debug`() {
    }

    @Test
    fun `getKeyboard$app_debug`() {
    }

    @Test
    fun `setKeyboard$app_debug`() {
    }

    @Test
    fun `getGameOverDialog$app_debug`() {
    }

    @Test
    fun `setGameOverDialog$app_debug`() {
    }
     */

/*
    // check win condition
    @Test
    fun isGameOver_win() {
        doubleNumberActivity.lifes = 3
        assertFalse(doubleNumberActivity.isGameOver)
    }

    // check loose condition
    @Test
    fun isGameOver_loose() {
        doubleNumberActivity.lifes = -1
        assertFalse(doubleNumberActivity.isGameOver)
    }


 */

    @Test
    fun checkPlayerInput() {
    }

    @Test
    fun checkCountdownExpired() {
    }

    @Test
    fun isComingHome() {
    }

    @Test
    fun updateProgressBar() {
    }

    @Test
    fun newChallenge() {
    }

    @Test
    fun onOptionsItemSelected() {
    }

    @Test
    fun onBackPressed() {
    }
}
