package eu.indiewalkabout.mathbrainer.presentation.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.unityads.bannerListener
import eu.indiewalkabout.mathbrainer.core.util.CountDownIndicator
import eu.indiewalkabout.mathbrainer.core.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityMathOpChooseResultBinding
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.presentation.ui.HomeGameActivity

class Math_Op_Choose_Result_Activity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityMathOpChooseResultBinding
    // private val unityAdsListener = UnityAdsListener()

    private lateinit var livesValueIv: ArrayList<ImageView>

    // store initial text color
    private var quizDefaultTextColor: ColorStateList? = null

    // numbers to be processed
    private var firstOperand: Int = 0
    private var secondOperand: Int = 0
    private var operation: Char = ' '

    // answer and its stuff
    private var answerOK: Int = 0
    private var correctBtnNumber = 1
    private val offset = 10
    private var pressedBtnValue = 0

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
    private var symbols = charArrayOf('+', '-', '*', '/')
    private lateinit var scoreType: String
    private val scoreTypeList = arrayOf(
        "sum_choose_result_game_score",
        "diff_choose_result_game_score", "mult_choose_result_game_score",
        "div_choose_result_game_score", "mix_choose_result_game_score"
    )

    // num of challenge to pass to next level
    // changing while level growing
    private var numChallengeEachLevel = 12
    private var countChallenge = 1

    // random range for answer btn number
    // changing while level growing
    private val minAnswerBtnNum = 3
    private val maxAnswerBtnNum = 9
    private var currentLevelAnswerBtnVisible = 3
    private var levelAnswerBtnTotalNum = 3

    // score var
    private var score = 0

    // countdown objects
    private lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength: Long = 20000
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // game over dialog
    private lateinit var gameOverDialog: GameOverDialog

    // unity bottom ads
    private var bottomBanner: BannerView? = null

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
        // setContentView(R.layout.activity_math_op_choose_result)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_math_op_choose_result)

        // Unity ads init
        // UnityAds.initialize(this, resources.getString(R.string.unityads_id), unityAdsListener)

        // Check if it's mixed op or single specific operation
        setOperationSymbol()

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        // binding.mAdView.loadAd(ConsentSDK.getAdRequest(this@Math_Op_Choose_Result_Activity))

        // Unity ads init
        // UnityAds.initialize(this, resources.getString(R.string.unityads_id), unityAdsListener)

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
            this@Math_Op_Choose_Result_Activity,
            binding.countdownBar, this@Math_Op_Choose_Result_Activity
        )

        // start with first challenge and countdown init
        newChallenge()

        // activate clicks on answer buttons
        setBtnPressedListener()

        // set first level
        updateLevel()

        hideStatusNavBars()

        showHighscore()

        // test unity ads
        bottomBanner = BannerView(this, "banner", UnityBannerSize(320, 50))
        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
        binding.bannerLayout.addView(bottomBanner)
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

    // Check and set the symbol of the operation from the caller intent
    private fun setOperationSymbol() {
        val intent = intent
        val operationSpec: CharArray
        if (intent.hasExtra(HomeGameActivity.OPERATION_KEY)) {
            operationSpec = intent.getStringExtra(HomeGameActivity.OPERATION_KEY)!!.toCharArray()
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
        binding.answer01Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer02Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer03Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer04Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer05Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer06Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer07Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer08Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.answer09Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        binding.backhomeImg.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            // MathBrainerUtility.showUnityAdsRandom(this@Math_Op_Choose_Result_Activity)
            // TODO: Show unity ads interstitial

            val intent = Intent(this@Math_Op_Choose_Result_Activity, HomeGameActivity::class.java)
            startActivity(intent)
        }
    }

    // Check if player input is right/wrong and update score
    override fun checkPlayerInput() {

        Log.d(TAG, "checkPlayerInput: pressedBtnValue : $pressedBtnValue")

        // check if result is ok...
        // if (pressedBtnValue != 0  && pressedBtnValue == answerOK) {
        if (pressedBtnValue == answerOK) {

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
        binding.instructionsTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.ok_str)
        binding.resultTv.setTextColor(Color.GREEN)
        binding.firstOperandTv.setTextColor(Color.GREEN)
        binding.secondOperandTv.setTextColor(Color.GREEN)
        binding.operationSymbolTv.setTextColor(Color.GREEN)
        binding.gridLayout.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")
    }

    // Show wrong in case of game lose
    private fun showWrongResult() {
        binding.instructionsTv.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.wrong_str) + " : " + answerOK
        binding.resultTv.setTextColor(Color.RED)
        binding.firstOperandTv.setTextColor(Color.RED)
        binding.secondOperandTv.setTextColor(Color.RED)
        binding.operationSymbolTv.setTextColor(Color.RED)
        binding.gridLayout.visibility = View.INVISIBLE

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
        binding.instructionsTv.visibility = View.VISIBLE
        binding.gridLayout.visibility = View.VISIBLE
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
        // clear wrong answers list
        wrongAnswer.clear()

        // reset the number of visible button
        currentLevelAnswerBtnVisible = levelAnswerBtnTotalNum

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
                answerOK = firstOperand + secondOperand

                // set operations value in view
                binding.operationSymbolTv.text = Character.toString(operation)

                // statistics
                Results.incrementGameResultsThread("sums")
            }

            '-' -> {
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, firstOperand)

                // store correct answer
                answerOK = firstOperand - secondOperand

                // set operations value in view
                binding.operationSymbolTv.text = Character.toString(operation)

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
                answerOK = firstOperand * secondOperand

                // set operations value in view
                binding.operationSymbolTv.text = "X"

                // statistics
                Results.incrementGameResultsThread("multiplications")
            }

            '/' -> {
                // set operands to be processed
                secondOperand = MathBrainerUtility.randRange_ApiCheck(divMin, divHMax)
                // store correct answer
                answerOK = MathBrainerUtility.randRange_ApiCheck(divMin, divLMax)
                firstOperand = answerOK * secondOperand

                // set operations value in view
                binding.operationSymbolTv.text = Character.toString(operation)

                // statistics
                Results.incrementGameResultsThread("divisions")
            }
            else -> {
            }
        }

        binding.firstOperandTv.text = firstOperand.toString()
        binding.secondOperandTv.text = secondOperand.toString()

        // setup answers on button
        setupAnswersBtn()
    }

    // Create setup correct answer and false answer on buttons
    private fun setupAnswersBtn() {
        // choose the button where put the correct answer
        correctBtnNumber = MathBrainerUtility.randRange_ApiCheck(minAnswerBtnNum, maxAnswerBtnNum)
        var tmpBtn: Button? = getTheBtnNumber(correctBtnNumber)
        tmpBtn!!.text = answerOK.toString()

        // visible answer button update
        currentLevelAnswerBtnVisible--

        // set wrong answer on the others
        for (i in 1..maxAnswerBtnNum) {
            if (i != correctBtnNumber) {
                when (operation) {

                    '+', '-' -> {
                        tmpBtn = getTheBtnNumber(i)
                        var result = 0
                        // repeat until find a result not already choosen
                        do {
                            result = Math.abs(randomOffsetSum())
                        } while (wrongAnswer.lastIndexOf(result) > 0)
                        wrongAnswer.add(result)

                        tmpBtn!!.text = result.toString()

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn)
                    }

                    '*' -> {
                        tmpBtn = getTheBtnNumber(i)
                        var result = 0
                        do {
                            result = Math.abs(randomOffsetMult())
                        } while (wrongAnswer.lastIndexOf(result) > 0)
                        wrongAnswer.add(result)

                        tmpBtn!!.text = result.toString()

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn)
                    }

                    '/' -> {
                        tmpBtn = getTheBtnNumber(i)
                        var result = 0
                        do {
                            result = Math.abs(randomOffsetSum())
                        } while (wrongAnswer.lastIndexOf(result) > 0)
                        wrongAnswer.add(result)

                        tmpBtn!!.text = result.toString()

                        // make btn visible based on num answer btn visible per level
                        setAnswerBtnVisibility(tmpBtn)
                    }
                    else -> {
                    }
                }
            } else { // the btn with the right answer must be alwys visible
                tmpBtn = getTheBtnNumber(correctBtnNumber)
                tmpBtn!!.visibility = View.VISIBLE
            }
        }
    }

    // Set btn visible or not depending on current level button visible
    // @param thisBtn
    private fun setAnswerBtnVisibility(thisBtn: Button) {

        val guess = MathBrainerUtility.randomSignChooser()
        if (guess > 0 && currentLevelAnswerBtnVisible > 0) {
            thisBtn.visibility = View.VISIBLE
            currentLevelAnswerBtnVisible--
        } else {
            thisBtn.visibility = View.INVISIBLE
        }
    }

    // Random answer for sum generator
    private fun randomOffsetSum(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, (offset * 1.5).toInt())
        return if (result in 1..3) {
            answerOK + MathBrainerUtility.randomSignChooser() * result
        } else answerOK + result
    }

    // Random answer for multiplication generator
    private fun randomOffsetMult(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, offset * 2)
        val sign = MathBrainerUtility.randomSignChooser()

        return if (result in 4..11) {
            if (sign > 0) {
                answerOK + MathBrainerUtility.randomSignChooser() * result
            } else {
                (answerOK + MathBrainerUtility.randomSignChooser().toDouble() * (10 + result).toDouble() * 0.1).toInt()
            }
        } else if (result > 11 && result <= 16) {
            if (sign > 0) {
                answerOK + MathBrainerUtility.randomSignChooser() * result
            } else {
                answerOK * (result * 0.1).toInt()
            }
        } else if (result > 16) {
            if (sign > 0) {
                answerOK + MathBrainerUtility.randomSignChooser() * result
            } else {
                (answerOK + MathBrainerUtility.randomSignChooser().toDouble() * (3 + result).toDouble() * 0.1).toInt()
            }
        } else
            answerOK + MathBrainerUtility.randomSignChooser() * result
    }

    // Return the button based on number
    // @param num
    // @return
    private fun getTheBtnNumber(num: Int): Button? {
        when (num) {
            1 -> return binding.answer01Btn
            2 -> return binding.answer02Btn
            3 -> return binding.answer03Btn
            4 -> return binding.answer04Btn
            5 -> return binding.answer05Btn
            6 -> return binding.answer06Btn
            7 -> return binding.answer07Btn
            8 -> return binding.answer08Btn
            9 -> return binding.answer09Btn
            else -> {
            }
        }
        return null
    }

    // Show end game message
    private fun endGame() {
        val handler = Handler()

        // reset counter
        countDownIndicator.countdownReset()

        // gridLayout.setVisibility(View.INVISIBLE);
        showWrongResult()

        val runnable = Runnable { showGameOverDialog() }
        handler.postDelayed(runnable, 500)
    }

    // Show gameover dialog
    private fun showGameOverDialog() {
        gameOverDialog = GameOverDialog(
            this,
            this@Math_Op_Choose_Result_Activity, this
        )

        hideLastQuiz()
    }

    // Hide quiz
    private fun hideLastQuiz() {
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

            // increase the number of visible answer button
            if (level < 9 && levelAnswerBtnTotalNum < 9) {
                levelAnswerBtnTotalNum++
            }

            // increase time accordingly, but slightly
            timerLength = timerLength + 5000
            Log.d(TAG, "updatingLevel: New Level! new min : " + min + " new max: " + max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec.")
        }

        // statistics
        Results.incrementGameResultsThread("level_upgrades")
    }

    // Unity ads listener
    /*private inner class UnityAdsListener : IUnityAdsListener {

        override fun onUnityAdsReady(s: String) {
        }

        override fun onUnityAdsStart(s: String) {
        }

        override fun onUnityAdsFinish(s: String, finishState: UnityAds.FinishState) {
        }

        override fun onUnityAdsError(unityAdsError: UnityAds.UnityAdsError, s: String) {
        }
    }*/

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
        // MathBrainerUtility.showUnityAdsRandom(this)
        // TODO: Show unity ads interstitial
    }
}
