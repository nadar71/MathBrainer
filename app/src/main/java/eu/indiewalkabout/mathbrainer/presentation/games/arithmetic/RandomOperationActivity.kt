package eu.indiewalkabout.mathbrainer.presentation.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityRandomOperationBinding
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity
import java.util.ArrayList

class RandomOperationActivity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityRandomOperationBinding
    private val unityAdsListener = UnityAdsListener()

    private lateinit var livesValueIv: ArrayList<ImageView>

    // store initial text color
    private var quizDefaultTextColor: ColorStateList? = null

    // numbers to be processed
    private var firstOperand: Int = 0
    private var secondOperand: Int = 0
    private var operation: Char = ' '

    // answer and its stuff
    private var answer: Int = 0
    private var operationOK: String? = null
    private val correctBtnNumber = 1
    private val offset = 10
    private var pressedBtnValue = ""

    // starting level
    private var level = 0

    // lives counter; 0 to gameover
    private var lives = 3

    // random range of number to be processed
    private var min = 1
    private var max = 100

    private val multMin = 1
    private var multHMax = 30
    private var multLMax = 15

    private val divMin = 1
    private var divHMax = 15
    private var divLMax = 11

    // store wring answer to avoid duplicates
    private lateinit var wrongAnswer: ArrayList<Int>

    // operation symbols
    private val symbols = charArrayOf('+', '-', '*', '/')

    // num of challenge to pass to next level
    // changing while level growing
    private var numChallengeEachLevel = 12
    private var countChallenge = 1

    // score var
    private var score = 0

    // countdown objects
    // internal var countdownBar: ProgressBar
    private lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength: Long = 20000
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // game session end dialog
    internal var endSessiondialog: EndGameSessionDialog? = null

    // game over dialog
    private lateinit var gameOverDialog: GameOverDialog

    // Update lives view and check if it's game over or not
    // @override of IGameFunctions isGameOver()
    // @return boolean  : return true/false in case of gameover/gamecontinuing
    // update life counts
    // statistics
    // Update UI
    // check game over condition
    // statistics
    // lives remaining >0, restart a new counter
    // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
    override val isGameOver: Boolean
        get() {
            lives--
            Results.incrementGameResultsThread("lives_missed")

            Log.d(TAG, "isGameOver: $lives")
            if (lives > -1) {
                livesValueIv[lives].visibility = View.INVISIBLE
            }
            if (lives <= 0) {
                endGame()
                Results.incrementGameResultsThread("games_played")
                Results.incrementGameResultsThread("games_lose")
                Results.updateGameResultHighscoreThread("random_op_game_score", score)
                Results.incrementGameResultByDeltaThread("global_score", score)

                return true
            } else {
                return false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_random_operation)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_random_operation)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.mAdView.loadAd(ConsentSDK.getAdRequest(this@RandomOperationActivity))

        // store quiz text color for later use
        quizDefaultTextColor = binding.firstOperandTv.textColors

        // init lives led images
        livesValueIv = ArrayList()
        livesValueIv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_03_iv) as ImageView)

        // define wrong answers storage
        wrongAnswer = ArrayList()

        // Create new count down indicator, without starting it
        countDownIndicator = CountDownIndicator(
            this@RandomOperationActivity,
            binding.countdownBar, this@RandomOperationActivity
        )

        // start with first challenge and countdown init
        newChallenge()

        // activate clicks on answer buttons
        setBtnPressedListener()

        // set first level
        updateLevel()

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
        if (intent.hasExtra(ChooseGameActivity.HIGHSCORE)) {
            val value = intent.getIntExtra(ChooseGameActivity.HIGHSCORE, -1)
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

    // Set up the button pressed listener and checking answers
    private fun setBtnPressedListener() {
        binding.answerPlusBtn.setOnClickListener { view ->
            // val b = view as Button
            pressedBtnValue = "+"
            checkPlayerInput()
        }

        binding.answerMinusBtn.setOnClickListener { view ->
            // val b = view as Button
            pressedBtnValue = "-"
            checkPlayerInput()
        }

        binding.answerMultBtn.setOnClickListener { view ->
            // val b = view as Button
            pressedBtnValue = "*"
            checkPlayerInput()
        }

        binding.answerDivBtn.setOnClickListener { view ->
            // val b = view as Button
            pressedBtnValue = "/"
            checkPlayerInput()
        }

        binding.backhomeImg.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@RandomOperationActivity)

            val intent = Intent(this@RandomOperationActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
    }

    // Check if player input is right/wrong and update score
    override fun checkPlayerInput() {

        Log.d(TAG, "checkPlayerInput: pressedBtnValue : $pressedBtnValue")

        // check if result is ok...
        if (!pressedBtnValue.isEmpty() && pressedBtnValue == operationOK) {

            updateScore()

            countChallenge++

            setOperationText()

            binding.operationSymbolTv.visibility = View.VISIBLE

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

            if (gameover == false) {
                // show result and start a new game session if allowed
                showResult(false)
            }
        }
    }

    // Set the currect symbol operation for multiplication: X for *
    private fun setOperationText() {
        if (operationOK == "*") {
            binding.operationSymbolTv.text = "X"
        } else {
            binding.operationSymbolTv.text = operationOK
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
        binding.instructionTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.ok_str)
        binding.resultTv.setTextColor(Color.GREEN)
        binding.firstOperandTv.setTextColor(Color.GREEN)
        binding.secondOperandTv.setTextColor(Color.GREEN)
        setOperationText()
        // operationSymbol_tv.setText(operationOK);
        binding.operationSymbolTv.setTextColor(Color.GREEN)
        binding.operationResultTv.setTextColor(Color.GREEN)
        binding.equalsSignTv.setTextColor(Color.GREEN)
        binding.lifeGridLayout.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")
    }

    // Show wrong in case of game lose
    private fun showWrongResult() {
        binding.instructionTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.wrong_str) + " : " + operationOK
        binding.resultTv.setTextColor(Color.RED)
        binding.firstOperandTv.setTextColor(Color.RED)
        binding.secondOperandTv.setTextColor(Color.RED)
        setOperationText()
        // operationSymbol_tv.setText(operationOK);
        binding.operationSymbolTv.setTextColor(Color.RED)
        binding.operationResultTv.setTextColor(Color.RED)
        binding.equalsSignTv.setTextColor(Color.RED)
        binding.lifeGridLayout.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ko")
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
        binding.instructionTv.visibility = View.VISIBLE
        binding.firstOperandTv.setTextColor(quizDefaultTextColor)
        binding.secondOperandTv.setTextColor(quizDefaultTextColor)
        binding.operationSymbolTv.setTextColor(quizDefaultTextColor)
        binding.operationResultTv.setTextColor(quizDefaultTextColor)
        binding.equalsSignTv.setTextColor(quizDefaultTextColor)
        binding.lifeGridLayout.visibility = View.VISIBLE
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
        Results.updateGameResultHighscoreThread("random_op_game_score", score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    // Update progress bar
    override fun updateProgressBar(progress: Int) {
        binding.countdownBar.progress = progress
    }

    // Set new challenge in view
    override fun newChallenge() {
        // clear wrong answers list
        wrongAnswer.clear()

        // reset the number of visible button
        // todo :delete
        // currentLevelAnswerBtnVisible = levelAnswerBtnTotalNum;

        // set operation to be processed
        operation = symbols[MathBrainerUtility.randRange_ApiCheck(0, symbols.size - 1)]

        // calculate the quiz operation
        calculateOperation()

        Log.d(TAG, "newChallenge: $countChallenge")

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval)
    }

    // Choose the right operands based on based on operation symbol,update UI, do calculation ,
    // and store the correct answer.
    private fun calculateOperation() {
        when (operation) {
            '+' -> {
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, max)

                // store correct answer
                answer = firstOperand + secondOperand

                // statistics
                Results.incrementGameResultsThread("sums")
            }

            '-' -> {
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, firstOperand)

                // store correct answer
                answer = firstOperand - secondOperand

                // statistics
                Results.incrementGameResultsThread("differences")
            }

            '*' -> {
                // set operands to be processed
                val guess = MathBrainerUtility.randRange_ApiCheck(1, 2)
                if (guess == 1) {
                    firstOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multHMax)
                    secondOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multLMax)
                } else {
                    firstOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multLMax)
                    secondOperand = MathBrainerUtility.randRange_ApiCheck(multMin, multHMax)
                }

                // store correct answer
                answer = firstOperand * secondOperand

                // statistics
                Results.incrementGameResultsThread("multiplications")
            }

            '/' -> {
                // set operands to be processed
                secondOperand = MathBrainerUtility.randRange_ApiCheck(divMin, divHMax)
                // store correct answer
                answer = MathBrainerUtility.randRange_ApiCheck(divMin, divLMax)
                firstOperand = answer * secondOperand

                // statistics
                Results.incrementGameResultsThread("divisions")
            }

            else -> {
            }
        }

        // right answer
        operationOK = Character.toString(operation)

        // set values in view
        binding.operationResultTv.text = answer.toString(answer)

        // hide correct operation
        // operationSymbol_tv.setText(operationOK);
        // operationSymbol_tv.setVisibility(View.INVISIBLE);
        binding.operationSymbolTv.text = resources.getString(R.string.question_mark)

        binding.firstOperandTv.text = firstOperand.toString(firstOperand)
        binding.secondOperandTv.text = secondOperand.toString(secondOperand)
    }

    // Show end game message
    private fun endGame() {
        val handler = Handler()

        // reset counter
        countDownIndicator.countdownReset()

        showWrongResult()

        val runnable = Runnable { showGameOverDialog() }
        handler.postDelayed(runnable, 500)
    }

    // Show gameover dialog
    private fun showGameOverDialog() {
        gameOverDialog = GameOverDialog(
            this,
            this@RandomOperationActivity, this
        )

        // hide input field
        binding.firstOperandTv.visibility = View.INVISIBLE
        binding.secondOperandTv.visibility = View.INVISIBLE
        binding.operationSymbolTv.visibility = View.INVISIBLE
        binding.operationResultTv.visibility = View.INVISIBLE
        binding.equalsSignTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.INVISIBLE
    }

    // Updating level
    private fun updateLevel() {
        // increment level
        level++

        binding.levelValueTv.text = level.toString()

        // increment level difficulty
        if (level > 1) {
            // for +, -
            min = max
            max = 100 * level + 50 * (level - 1)

            // for *,/
            multHMax += 5
            multLMax += 1

            divHMax += 2
            divLMax += 1

            numChallengeEachLevel += 5

            // increase the number of visible answer button
            // if (level < 9 ) levelAnswerBtnTotalNum++;

            // increase time accordingly, but slightly
            timerLength = timerLength + 5000
            Log.d(
                TAG,
                "updatingLevel: New Level! new min : " + min + " new max: " + max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec."
            )
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

    // ---------------------------------------------------------------------------------------------
    // MENU STUFF
    // ---------------------------------------------------------------------------------------------
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
