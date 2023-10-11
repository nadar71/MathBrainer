package eu.indiewalkabout.mathbrainer.presentation.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import java.util.ArrayList
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.core.util.CountDownIndicator
import eu.indiewalkabout.mathbrainer.core.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.MyKeyboard


import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityMathOpWriteResultBinding
import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity


class Math_Op_Write_Result_Activity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityMathOpWriteResultBinding
    private val unityAdsListener = UnityAdsListener()

    private lateinit var livesValueIv: ArrayList<ImageView>

    // store initial text color
    private var quizDefaultTextColor: ColorStateList? = null


    // numbers to be processed
    private var firstOperand: Int = 0
    private var secondOperand: Int = 0
    private var operation: Char = ' '

    // correct answer
    private var answerOK: Int = 0

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

    private lateinit var symbols: CharArray // = {'+','-','*','/'};
    private lateinit var scoreType: String
    private val scoreTypeList = arrayOf(
        "sum_write_result_game_score",
        "diff_write_result_game_score",
        "mult_write_result_game_score",
        "div_write_result_game_score",
        "mix_write_result_game_score"
    )

    // num of challenge to pass to next level
    // changing while level growing
    private var numChallengeEachLevel = 12
    private var countChallenge = 1

    // score var
    private var score = 0

    // countdown objects
    // internal var countdownBar: ProgressBar
    internal lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength: Long = 20000
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // custom keyboard instance
    internal lateinit var keyboard: MyKeyboard

    // game over dialog
    internal lateinit var gameOverDialog: GameOverDialog


    // Update lives view and check if it's game over or not
    // @override of IGameFunctions isGameOver()
    // @return boolean  : return true/false in case of game over/game continuing
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
                Results.updateGameResultHighscoreThread(scoreType, score)
                Results.incrementGameResultByDeltaThread("global_score", score)

                return true

            } else {
                return false
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_math_op_write_result)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_math_op_write_result)


        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // Check if it's mixed op or single specific operation
        setOperationSymbol()


        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle
        // the right way to load the ad
        binding.mAdView.loadAd(ConsentSDK.getAdRequest(this@Math_Op_Write_Result_Activity))


        // store quiz text color for later use
        quizDefaultTextColor = binding.firstOperandTv.textColors


        // init lives led images
        livesValueIv = ArrayList()
        livesValueIv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_03_iv) as ImageView)


        // keyboard
        setupCustomKeyboard()

        // Create new count down indicator, without starting it
        countDownIndicator = CountDownIndicator(
            this@Math_Op_Write_Result_Activity,
            binding.countdownBar, this@Math_Op_Write_Result_Activity
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
            MathBrainerUtility.showUnityAdsRandom(this@Math_Op_Write_Result_Activity)

            val intent = Intent(this@Math_Op_Write_Result_Activity, ChooseGameActivity::class.java)
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
        if (intent.hasExtra(ChooseGameActivity.HIGHSCORE)) {
            val value = intent.getIntExtra(ChooseGameActivity.HIGHSCORE, -1)
            binding.highscoreValueTv.text = Integer.toString(value)
        } else {
            binding.highscoreValueTv.text = "0"
        }
    }

    // Check and set the symbol of the operation from the caller intent
    private fun setOperationSymbol() {
        val intent = intent
        val operationSpec: CharArray
        if (intent.hasExtra(ChooseGameActivity.OPERATION_KEY)) {
            operationSpec = intent.getStringExtra(ChooseGameActivity.OPERATION_KEY).toCharArray()
            when (operationSpec[0]) {
                '+' -> {
                    symbols = CharArray(1)
                    symbols[0] = '+'
                    scoreType = scoreTypeList[0]
                }

                '-' -> {
                    symbols = CharArray(1)
                    symbols[0] = '-'
                    scoreType = scoreTypeList[1]
                }

                '*' -> {
                    symbols = CharArray(1)
                    symbols[0] = '*'
                    scoreType = scoreTypeList[2]
                }

                '/' -> {
                    symbols = CharArray(1)
                    symbols[0] = '/'
                    scoreType = scoreTypeList[3]
                }

                else -> {
                }
            }
        } else {
            symbols = CharArray(4)
            symbols[0] = '+'
            symbols[1] = '-'
            symbols[2] = '*'
            symbols[3] = '/'
            scoreType = scoreTypeList[4]
        }
    }


    // Make bottom navigation bar and status bar hide, without resize when reappearing
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
        keyboard.setInputConnection(ic, this@Math_Op_Write_Result_Activity)
    }


    // Check if player input is right/wrong and update score
    override fun checkPlayerInput() {
        var inputNum = 0

        // get the player input
        val tmp = binding.playerInputEt.text.toString()

        // stop timer
        countDownIndicator.countdownReset()

        // nothing inserted, ignore
        if (tmp.isEmpty()) {
            return
        }

        inputNum = Integer.parseInt(tmp)

        Log.d(TAG, "checkPlayerInput: inputNum : $inputNum")

        // check if result is ok...
        // if (inputNum != 0  && inputNum == answerOK) {
        if (inputNum == answerOK) {
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


    // Show the result of the game session
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
        binding.firstOperandTv.setTextColor(Color.GREEN)
        binding.secondOperandTv.setTextColor(Color.GREEN)
        binding.operationSymbolTv.setTextColor(Color.GREEN)
        binding.playerInputEt.setTextColor(Color.GREEN)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")
    }


    // Show wrong in case of game lose
    private fun showWrongResult() {
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.wrong_str) + " : " + answerOK
        binding.resultTv.setTextColor(Color.RED)
        binding.firstOperandTv.setTextColor(Color.RED)
        binding.secondOperandTv.setTextColor(Color.RED)
        binding.operationSymbolTv.setTextColor(Color.RED)
        binding.playerInputEt.setTextColor(Color.RED)
        // hide keyboard
        keyboard.visibility = View.INVISIBLE

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
        binding.firstOperandTv.setTextColor(quizDefaultTextColor)
        binding.secondOperandTv.setTextColor(quizDefaultTextColor)
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
        binding.scoreValueTv.text = score.toString()
    }


    // Saves the score and others game data if coming back home before reach game over condition
    fun isComingHome() {
        Results.updateGameResultHighscoreThread(scoreType, score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    // Update progress bar
    override fun updateProgressBar(progress: Int) {
        binding.countdownBar.progress = progress
    }


    // Set new challenge in view
    override fun newChallenge() {
        // set operation to be processed; general case symbols.length-1 > 1
        operation = symbols[MathBrainerUtility.randRange_ApiCheck(0, symbols.size - 1)]

        // calculate the quiz operation
        calculateOperation()

        // clean edit text field
        binding.playerInputEt.isFocused
        binding.playerInputEt.setText("")
        Log.d(TAG, "newChallenge: $countChallenge")

        // reset countdown if any and restart if
        countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval)

    }

    // Choose the right operands based on based on operation symbol,update UI, do calculation ,
    // and store the correct answer.
    private fun calculateOperation() {
        when (operation) {
            '+' -> {
                //operationSymbol_tv.setText("+");
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, max)

                // store correct answer
                answerOK = firstOperand + secondOperand

                // statistics
                Results.incrementGameResultsThread("sums")

                binding.operationSymbolTv.text = Character.toString(operation)
            }

            '-' -> {
                //operationSymbol_tv.setText("-");
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, firstOperand)

                // store correct answer
                answerOK = firstOperand - secondOperand

                binding.operationSymbolTv.text = Character.toString(operation)

                // statistics
                Results.incrementGameResultsThread("differences")
            }

            '*' -> {
                //operationSymbol_tv.setText("*");
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
                answerOK = firstOperand * secondOperand

                binding.operationSymbolTv.text = "X"

                // statistics
                Results.incrementGameResultsThread("multiplications")
            }

            '/' -> {
                //operationSymbol_tv.setText("/");
                // set operands to be processed
                secondOperand = MathBrainerUtility.randRange_ApiCheck(divMin, divHMax)
                // store correct answer
                answerOK = MathBrainerUtility.randRange_ApiCheck(divMin, divLMax)
                firstOperand = answerOK * secondOperand

                binding.operationSymbolTv.text = Character.toString(operation)

                // statistics
                Results.incrementGameResultsThread("divisions")
            }

            else -> {
            }
        }


        binding.firstOperandTv.text = firstOperand.toString()
        binding.secondOperandTv.text = secondOperand.toString()

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
            this@Math_Op_Write_Result_Activity, this
        )

        hideLastQuiz()

    }


    // Hide quiz
    private fun hideLastQuiz() {
        binding.playerInputEt.visibility = View.INVISIBLE
        binding.firstOperandTv.visibility = View.INVISIBLE
        binding.secondOperandTv.visibility = View.INVISIBLE
        binding.operationSymbolTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.INVISIBLE
    }


    // Updating level
    private fun updateLevel() {
        // increment level
        level++

        binding.levelValueTv.text = Integer.toString(level)

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
