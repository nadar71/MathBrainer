package eu.indiewalkabout.mathbrainer.presentation.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView


import java.util.ArrayList

import eu.indiewalkabout.mathbrainer.presentation.ui.HomeGameActivity
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.util.*

import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.core.util.*
import kotlinx.android.synthetic.main.activity_double_number.*


/**
 * ---------------------------------------------------------------------------------------------
 * Given a number, write its double
 * ---------------------------------------------------------------------------------------------
 */
class DoubleNumberActivity : AppCompatActivity(), IGameFunctions {

    private val unityAdsListener = UnityAdsListener()

    private lateinit var lifesValue_iv: ArrayList<ImageView>

    // store initial text color
    private var quizDefaultTextColor: ColorStateList? = null

    // number to be doubled
    private var numToBeDoubled: Int = 0

    // starting level
    private var level = 0

    // lifes counter; 0 to gameover
    internal var lifes = 3

    // random range of number to be doubled
    private var min = 1
    private var max = 100

    // num of challenge to be in the test
    private val numChallengeEachLevel = 10
    private var countChallenge = 1

    // score var
    private var score = 0

    // countdown objects
    // internal var countdownBar: ProgressBar
    internal lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // game session end dialog
    internal var endSessiondialog: EndGameSessionDialog? = null

    // custom keyboard instance
    internal lateinit var keyboard: MyKeyboard

    // game over dialog
    internal lateinit var gameOverDialog: GameOverDialog


    /**
     * ---------------------------------------------------------------------------------------------
     * Update lifes view and check if it's game over or not
     * @override of IGameFunctions isGameOver()
     * @return boolean  : return true/false in case of gameover/gamecontinuing
     * ---------------------------------------------------------------------------------------------
     */
    override val isGameOver: Boolean
        get() {
            // update life counts
            lifes--

            // statistics
            Results.incrementGameResultsThread("lifes_missed")

            // Update UI
            Log.d(eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity.Companion.TAG, "isGameOver: $lifes")
            if (lifes > -1) {
                lifesValue_iv[lifes].visibility = View.INVISIBLE
            }

            // check game over condition
            if (lifes <= 0) {
                endGame()
                // statistics
                Results.incrementGameResultsThread("games_played")
                Results.incrementGameResultsThread("games_lose")
                Results.updateGameResultHighscoreThread("doublenumber_game_score", score)
                Results.incrementGameResultByDeltaThread("global_score", score)

                return true

            } else {
                // lifes remaining >0, restart a new counter
                // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
                return false
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_double_number)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(this@DoubleNumberActivity))

        highscore_value_tv.text = "-1" // debug init

        // store quiz text color for later use
        quizDefaultTextColor = numberToBeDoubled_tv.textColors

        // init lifes led images
        lifesValue_iv = ArrayList()
        lifesValue_iv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_03_iv) as ImageView)

        // keyboard
        setupCustomKeyboard()

        // Create new count down indicator, without starting it
        countDownIndicator = CountDownIndicator(this@DoubleNumberActivity,
                countdownBar, this@DoubleNumberActivity)


        // start with first challenge and countdown init
        newChallenge()

        // set first level
        updateLevel()

        // set listener on DONE button on soft keyboard to get the player input
        playerInput_et.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPlayerInput()
                    return true
                }
                return false
            }
        })

        backhome_img.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@DoubleNumberActivity)

            val intent = Intent(this@DoubleNumberActivity, HomeGameActivity::class.java)
            startActivity(intent)
        }

        hideStatusNavBars()

        showHighscore()

    }


    override fun onResume() {
        super.onResume()
        // make bottom navigation bar and status bar hide
        hideStatusNavBars()
        newChallenge()
    }


    override fun onPause() {
        super.onPause()
        countDownIndicator.countdownReset()

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set the highscore passed from main
     * ---------------------------------------------------------------------------------------------
     */
    private fun showHighscore() {
        val intent = intent
        if (intent.hasExtra(HomeGameActivity.HIGHSCORE)) {
            val value = intent.getIntExtra(HomeGameActivity.HIGHSCORE, -1)
            highscore_value_tv.text = Integer.toString(value)
        } else {
            highscore_value_tv.text = "0"
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar

                // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN) // // hide status bar
        decorView.systemUiVisibility = uiOptions
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create  and setup customkeyboard
     * ---------------------------------------------------------------------------------------------
     */
    private fun setupCustomKeyboard() {
        // init custom keyboard
        keyboard = findViewById<View>(R.id.keyboard) as MyKeyboard

        // prevent system keyboard from appearing when EditText is tapped
        playerInput_et.setOnTouchListener { v, event ->
            val inType = playerInput_et.inputType // backup the input type
            playerInput_et.inputType = InputType.TYPE_NULL // disable soft input
            playerInput_et.onTouchEvent(event) // call native handler
            playerInput_et.inputType = inType // restore input type
            playerInput_et.setTextIsSelectable(false)
            true // consume touch even
        }

        // pass the InputConnection from the EditText to the keyboard
        val ic = playerInput_et.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic, this@DoubleNumberActivity)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * ---------------------------------------------------------------------------------------------
     */
    override fun checkPlayerInput() {
        var inputNum = 0

        // get the player input
        val tmp = playerInput_et.text.toString()

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return
        }

        inputNum = Integer.parseInt(tmp)

        Log.d(eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity.Companion.TAG, "checkPlayerInput: inputNum : $inputNum")

        // check if result is ok...
        if (inputNum != 0 && inputNum == 2 * numToBeDoubled) {

            updateScore()

            countChallenge++

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel) {
                // reset counter
                countChallenge = 0

                updateLevel()
            }

            // show result and start a new game session if allowed
            showResult(true)

            // ...otherwise a life will be lost
        } else {

            // lose a life, check if it's game over
            val gameover = isGameOver

            // new number to double
            if (gameover == false) {
                // show result and start a new game session if allowed
                showResult(false)
            }

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check state at countdown expired
     * ---------------------------------------------------------------------------------------------
     */
    override fun checkCountdownExpired() {

        // lose a life, check if it's game over
        val gameover = isGameOver

        // new number to double
        if (gameover == false) {
            // show result and start a new game session if allowed
            showResult(false)
        }

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show the result of the
     * ---------------------------------------------------------------------------------------------
     */
    private fun showResult(win: Boolean) {

        if (win == true) {
            showOkResult()
            newchallengeAfterTimerLength(1000)


        } else {
            showWrongResult()
            newchallengeAfterTimerLength(1000)

        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Show ok in case of game win
     * ---------------------------------------------------------------------------------------------
     */
    private fun showOkResult() {
        result_tv.visibility = View.VISIBLE
        result_tv.text = resources.getString(R.string.ok_str)
        result_tv.setTextColor(Color.GREEN)
        numberToBeDoubled_tv.setTextColor(Color.GREEN)
        operationSymbol_tv.setTextColor(Color.GREEN)
        playerInput_et.setTextColor(Color.GREEN)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")

        Results.incrementGameResultsThread("doublings")
        Results.incrementGameResultsThread("multiplications")

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show wrong in case of game lose
     * ---------------------------------------------------------------------------------------------
     */
    private fun showWrongResult() {
        result_tv.visibility = View.VISIBLE
        result_tv.text = resources.getString(R.string.wrong_str) + " : " + 2 * numToBeDoubled
        result_tv.setTextColor(Color.RED)
        numberToBeDoubled_tv.setTextColor(Color.RED)
        operationSymbol_tv.setTextColor(Color.RED)
        playerInput_et.setTextColor(Color.RED)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ko")

        Results.incrementGameResultsThread("doublings")
        Results.incrementGameResultsThread("multiplications")
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Launch new challenge after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private fun newchallengeAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable {
            setupBeforeNewGame()
            newChallenge()
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Setup before new game session
     * ---------------------------------------------------------------------------------------------
     */
    private fun setupBeforeNewGame() {
        result_tv.visibility = View.INVISIBLE
        numberToBeDoubled_tv.setTextColor(quizDefaultTextColor)
        operationSymbol_tv.setTextColor(quizDefaultTextColor)
        playerInput_et.setTextColor(quizDefaultTextColor)
        keyboard.visibility = View.VISIBLE
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Update score view
     * ---------------------------------------------------------------------------------------------
     */
    private fun updateScore() {
        highscore_label_tv.visibility = View.INVISIBLE
        highscore_value_tv.visibility = View.INVISIBLE
        scoreLabel_tv.visibility = View.VISIBLE
        scoreValue_tv.visibility = View.VISIBLE

        score += 25
        scoreValue_tv.text = Integer.toString(score)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Saves the score and others game data if coming back home before reach game over condition
     * ---------------------------------------------------------------------------------------------
     */
    fun isComingHome() {
        Results.updateGameResultHighscoreThread("doublenumber_game_score", score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Update progress bar
     * ---------------------------------------------------------------------------------------------
     */
    override fun updateProgressBar(progress: Int) {
        countdownBar.progress = progress
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set new challenge in view
     * ---------------------------------------------------------------------------------------------
     */
    override fun newChallenge() {
        // set the number to be doubled
        numToBeDoubled = MathBrainerUtility.randRange_ApiCheck(min, max)
        numberToBeDoubled_tv.text = Integer.toString(numToBeDoubled)

        // clean edit text field
        playerInput_et.isFocused
        playerInput_et.setText("")
        Log.d(eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity.Companion.TAG, "newChallenge: $countChallenge")

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval)

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show end game message
     * ---------------------------------------------------------------------------------------------
     */
    private fun endGame() {
        val handler = Handler()

        showWrongResult()

        // reset counter
        countDownIndicator.countdownReset()

        val runnable = Runnable { showGameOverDialog() }
        handler.postDelayed(runnable, 500)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show gameover dialog
     * ---------------------------------------------------------------------------------------------
     */
    private fun showGameOverDialog() {
        gameOverDialog = GameOverDialog(this,
                this@DoubleNumberActivity, this)

        hideLastQuiz()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide quiz
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideLastQuiz() {
        playerInput_et.visibility = View.INVISIBLE
        numberToBeDoubled_tv.visibility = View.INVISIBLE
        operationSymbol_tv.visibility = View.INVISIBLE
        operationSymbol_tv.visibility = View.INVISIBLE
        result_tv.visibility = View.INVISIBLE
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Updating level
     * ---------------------------------------------------------------------------------------------
     */
    private fun updateLevel() {
        // increment level
        level++

        levelValue_tv.text = Integer.toString(level)

        // increment level difficulty
        if (level > 1) {
            min = max
            max = 100 * level + 50 * (level - 1)

            // increase time accordingly, but slightly
            timerLength = timerLength + 1000
            Log.d(eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity.Companion.TAG, "updatingLevel: New Level! new min : " + min + " new max: " + max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec.")
        }

        // statistics
        Results.incrementGameResultsThread("level_upgrades")

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Unity ads listener
     * ---------------------------------------------------------------------------------------------
     */
    private inner class UnityAdsListener : IUnityAdsListener {

        override fun onUnityAdsReady(s: String) {

        }

        override fun onUnityAdsStart(s: String) {

        }

        override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {

        }

        override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {


            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        // reset and destroy counter
        countDownIndicator.countdownReset()

        // saves score
        isComingHome()

        // show unityads randomic
        MathBrainerUtility.showUnityAdsRandom(this)

    }

    companion object {

        private val TAG = eu.indiewalkabout.mathbrainer.presentation.games.arithmetic.DoubleNumberActivity::class.java.simpleName
    }


}
