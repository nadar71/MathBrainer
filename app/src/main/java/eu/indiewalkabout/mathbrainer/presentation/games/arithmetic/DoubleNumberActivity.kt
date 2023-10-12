package eu.indiewalkabout.mathbrainer.presentation.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.core.util.CountDownIndicator
import eu.indiewalkabout.mathbrainer.core.util.EndGameSessionDialog
import eu.indiewalkabout.mathbrainer.core.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.MyKeyboard
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityDoubleNumberBinding
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.presentation.ui.HomeGameActivity
import java.util.ArrayList

// Given a number, write its double
class DoubleNumberActivity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityDoubleNumberBinding

    private val unityAdsListener = UnityAdsListener()

    private lateinit var livesValueIv: ArrayList<ImageView>

    // store initial text color
    private var quizDefaultTextColor: ColorStateList? = null

    // number to be doubled
    private var numToBeDoubled: Int = 0

    // starting level
    private var level = 0

    // lives counter; 0 to gameover
    private var lives = 3

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
    private lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength = CountDownIndicator.DEFAULT_MILLISINFUTURE
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // game session end dialog
    internal var endSessiondialog: EndGameSessionDialog? = null

    // custom keyboard instance
    private lateinit var keyboard: MyKeyboard

    // game over dialog
    private lateinit var gameOverDialog: GameOverDialog

    // Update lives view and check if it's game over or not
    // @override of IGameFunctions isGameOver()
    // @return boolean  : return true/false in case of game over/game continuing
    override val isGameOver: Boolean
        get() {
            // update life counts
            lives--

            // statistics
            Results.incrementGameResultsThread("lives_missed")

            // Update UI
            Log.d(TAG, "isGameOver: $lives")
            if (lives > -1) {
                livesValueIv[lives].visibility = View.INVISIBLE
            }

            // check game over condition
            if (lives <= 0) {
                endGame()
                // statistics
                Results.incrementGameResultsThread("games_played")
                Results.incrementGameResultsThread("games_lose")
                Results.updateGameResultHighscoreThread("doublenumber_game_score", score)
                Results.incrementGameResultByDeltaThread("global_score", score)

                return true
            } else {
                // lives remaining >0, restart a new counter
                // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
                return false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_double_number)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_double_number)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.mAdView.loadAd(ConsentSDK.getAdRequest(this@DoubleNumberActivity))

        binding.highscoreValueTv.text = "-1" // debug init

        // store quiz text color for later use
        quizDefaultTextColor = binding.numberToBeDoubledTv.textColors

        // init lives led images
        livesValueIv = ArrayList()
        livesValueIv.add(binding.life01Iv) as ImageView
        livesValueIv.add(binding.life02Iv) as ImageView
        livesValueIv.add(binding.life03Iv) as ImageView

        // keyboard
        setupCustomKeyboard()

        // Create new count down indicator, without starting it
        countDownIndicator = CountDownIndicator(
            this@DoubleNumberActivity,
            binding.countdownBar, this@DoubleNumberActivity
        )

        // start with first challenge and countdown init
        newChallenge()

        // set first level
        updateLevel()

        // set listener on DONE button on soft keyboard to get the player input
        binding.playerInputEt.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    checkPlayerInput()
                    return true
                }
                return false
            }
        })

        binding.backhomeImg.setOnClickListener {
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

    // Set the highscore passed from main
    private fun showHighscore() {
        val intent = intent
        if (intent.hasExtra(HomeGameActivity.HIGHSCORE)) {
            val value = intent.getIntExtra(HomeGameActivity.HIGHSCORE, -1)
            binding.highscoreValueTv.text = value.toString()
        } else {
            binding.highscoreValueTv.text = "0"
        }
    }

    // Make bottom navigation bar and status bar hide, without resize when reappearing
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView
        val uiOptions = (
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navigation bar

                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide navigation bar

                // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            ) // // hide status bar
        decorView.systemUiVisibility = uiOptions
    }

    // Create  and setup customkeyboard
    private fun setupCustomKeyboard() {
        // init custom keyboard
        keyboard = findViewById<View>(R.id.keyboard) as MyKeyboard

        // prevent system keyboard from appearing when EditText is tapped
        binding.playerInputEt.setOnTouchListener { v, event ->
            val inType = binding.playerInputEt.inputType // backup the input type
            binding.playerInputEt.inputType = InputType.TYPE_NULL // disable soft input
            binding.playerInputEt.onTouchEvent(event) // call native handler
            binding.playerInputEt.inputType = inType // restore input type
            binding.playerInputEt.setTextIsSelectable(false)
            true // consume touch even
        }

        // pass the InputConnection from the EditText to the keyboard
        val ic = binding.playerInputEt.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic, this@DoubleNumberActivity)
    }

    // Check if player input is right/wrong and update score
    override fun checkPlayerInput() {
        var inputNum = 0

        // get the player input
        val tmp = binding.playerInputEt.text.toString()

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return
        }

        inputNum = Integer.parseInt(tmp)

        Log.d(TAG, "checkPlayerInput: inputNum : $inputNum")

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
            if (!gameover) {
                // show result and start a new game session if allowed
                showResult(false)
            }
        }
    }

    // Check state at countdown expired
    override fun checkCountdownExpired() {

        // lose a life, check if it's game over
        val gameover = isGameOver

        // new number to double
        if (!gameover) {
            // show result and start a new game session if allowed
            showResult(false)
        }
    }

    // Show the result of the
    private fun showResult(win: Boolean) {

        if (win) {
            showOkResult()
            newchallengeAfterTimerLength(1000)
        } else {
            showWrongResult()
            newchallengeAfterTimerLength(1000)
        }
    }

    // Show ok in case of game win
    private fun showOkResult() {
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.ok_str)
        binding.resultTv.setTextColor(Color.GREEN)
        binding.numberToBeDoubledTv.setTextColor(Color.GREEN)
        binding.operationSymbolTv.setTextColor(Color.GREEN)
        binding.playerInputEt.setTextColor(Color.GREEN)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")

        Results.incrementGameResultsThread("doublings")
        Results.incrementGameResultsThread("multiplications")
    }

    // Show wrong in case of game lose
    private fun showWrongResult() {
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.wrong_str) + " : " + 2 * numToBeDoubled
        binding.resultTv.setTextColor(Color.RED)
        binding.numberToBeDoubledTv.setTextColor(Color.RED)
        binding.operationSymbolTv.setTextColor(Color.RED)
        binding.playerInputEt.setTextColor(Color.RED)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ko")

        Results.incrementGameResultsThread("doublings")
        Results.incrementGameResultsThread("multiplications")
    }

    // Launch new challenge after timerLength
    private fun newchallengeAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable {
            setupBeforeNewGame()
            newChallenge()
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }

    // Setup before new game session
    private fun setupBeforeNewGame() {
        binding.resultTv.visibility = View.INVISIBLE
        binding.numberToBeDoubledTv.setTextColor(quizDefaultTextColor)
        binding.operationSymbolTv.setTextColor(quizDefaultTextColor)
        binding.playerInputEt.setTextColor(quizDefaultTextColor)
        keyboard.visibility = View.VISIBLE
    }

    // Update score view
    private fun updateScore() {
        binding.highscoreLabelTv.visibility = View.INVISIBLE
        binding.highscoreValueTv.visibility = View.INVISIBLE
        binding.scoreLabelTv.visibility = View.VISIBLE
        binding.scoreValueTv.visibility = View.VISIBLE

        score += 25
        binding.scoreValueTv.text = Integer.toString(score)
    }

    // Saves the score and others game data if coming back home before reach game over condition
    fun isComingHome() {
        Results.updateGameResultHighscoreThread("doublenumber_game_score", score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    // Update progress bar
    override fun updateProgressBar(progress: Int) {
        binding.countdownBar.progress = progress
    }

    // Set new challenge in view
    override fun newChallenge() {
        // set the number to be doubled
        numToBeDoubled = MathBrainerUtility.randRange_ApiCheck(min, max)
        binding.numberToBeDoubledTv.text = numToBeDoubled.toString()

        // clean edit text field
        binding.playerInputEt.isFocused
        binding.playerInputEt.setText("")
        Log.d(TAG, "newChallenge: $countChallenge")

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval)
    }

    // Show end game message
    private fun endGame() {
        val handler = Handler()

        showWrongResult()

        // reset counter
        countDownIndicator.countdownReset()

        val runnable = Runnable { showGameOverDialog() }
        handler.postDelayed(runnable, 500)
    }

    // Show gameover dialog
    private fun showGameOverDialog() {
        gameOverDialog = GameOverDialog(
            this,
            this@DoubleNumberActivity, this
        )

        hideLastQuiz()
    }

    // Hide quiz
    private fun hideLastQuiz() {
        binding.playerInputEt.visibility = View.INVISIBLE
        binding.numberToBeDoubledTv.visibility = View.INVISIBLE
        binding.operationSymbolTv.visibility = View.INVISIBLE
        binding.operationSymbolTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.INVISIBLE
    }

    // Updating level
    private fun updateLevel() {
        // increment level
        level++

        binding.levelValueTv.text = level.toString()

        // increment level difficulty
        if (level > 1) {
            min = max
            max = 100 * level + 50 * (level - 1)

            // increase time accordingly, but slightly
            timerLength += 1000
            Log.d(TAG, "updatingLevel: New Level! new min : " + min + " new max: " + max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec.")
        }

        // statistics
        Results.incrementGameResultsThread("level_upgrades")
    }

    // Unity ads listener
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

    // MENU STUFF
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
}
