package eu.indiewalkabout.mathbrainer.games.arithmetic

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView

import java.util.ArrayList

import eu.indiewalkabout.mathbrainer.ui.ChooseGameActivity
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.statistics.Results
import eu.indiewalkabout.mathbrainer.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.util.CountDownIndicator
import eu.indiewalkabout.mathbrainer.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility

import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import kotlinx.android.synthetic.main.activity_math_op_choose_result.*


class Math_Op_Choose_Result_Activity : AppCompatActivity(), IGameFunctions {

    private val unityAdsListener = UnityAdsListener()

    private lateinit var lifesValue_iv: ArrayList<ImageView>

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

    // lifes counter; 0 to gameover
    private var lifes = 3

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
    internal lateinit var wrongAnswer: ArrayList<Int>

    // operation symbols
    private var symbols = charArrayOf('+', '-', '*', '/')
    private lateinit var scoreType: String
    private val scoreTypeList = arrayOf("sum_choose_result_game_score", "diff_choose_result_game_score", "mult_choose_result_game_score", "div_choose_result_game_score", "mix_choose_result_game_score")

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
    internal lateinit var countDownIndicator: CountDownIndicator

    // max time, increased by level growing
    private var timerLength: Long = 20000
    private val timerCountDownInterval = CountDownIndicator.DEFAULT_COUNTDOWNINTERVAL

    // game over dialog
    internal lateinit var gameOverDialog: GameOverDialog


    /**
     * ---------------------------------------------------------------------------------------------
     * Update lifes view and check if it's game over or not
     * @override of IGameFunctions isGameOver()
     * @return boolean  : return true/false in case of gameover/gamecontinuing
     * ---------------------------------------------------------------------------------------------
     */
    override// update life counts
    // statistics
    // Update UI
    // check game over condition
    // statistics
    // lifes remaining >0, restart a new counter
    // countDownIndicator.countdownBarStart(timerLength, timerCountDownInterval);
    val isGameOver: Boolean
        get() {
            lifes--
            Results.incrementGameResultsThread("lifes_missed")

            Log.d(TAG, "isGameOver: $lifes")
            if (lifes > -1) {
                lifesValue_iv[lifes].visibility = View.INVISIBLE
            }
            if (lifes <= 0) {
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
        setContentView(R.layout.activity_math_op_choose_result)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // Check if it's mixed op or single specific operation
        setOperationSymbol()


        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(this@Math_Op_Choose_Result_Activity))

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)


        // store quiz text color for later use
        quizDefaultTextColor = firstOperand_tv.textColors

        // init lifes led images
        lifesValue_iv = ArrayList()
        lifesValue_iv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_03_iv) as ImageView)

        // define wrong answers storage
        wrongAnswer = ArrayList()


        // Create new count down indicator, without starting it
        countDownIndicator = CountDownIndicator(this@Math_Op_Choose_Result_Activity,
                countdownBar, this@Math_Op_Choose_Result_Activity)

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


    /**
     * ---------------------------------------------------------------------------------------------
     * Set the highscore passed from main
     * ---------------------------------------------------------------------------------------------
     */
    private fun showHighscore() {
        val intent = intent
        if (intent.hasExtra(ChooseGameActivity.HIGHSCORE)) {
            val value = intent.getIntExtra(ChooseGameActivity.HIGHSCORE, -1)
            highscore_value_tv.text = Integer.toString(value)
        } else {
            highscore_value_tv.text = "0"
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check and set the symbol of the operation from the caller intent
     * ---------------------------------------------------------------------------------------------
     */
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
     * Set up the button pressed listener and checking answers
     * ---------------------------------------------------------------------------------------------
     */
    private fun setBtnPressedListener() {
        answer01Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer02Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer03Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer04Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer05Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer06Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer07Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer08Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer09Btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        backhome_img.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@Math_Op_Choose_Result_Activity)

            val intent = Intent(this@Math_Op_Choose_Result_Activity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check if player input is right/wrong and update score
     * ---------------------------------------------------------------------------------------------
     */
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
        instructions_tv.visibility = View.INVISIBLE
        result_tv.visibility = View.VISIBLE
        result_tv.text = resources.getString(R.string.ok_str)
        result_tv.setTextColor(Color.GREEN)
        firstOperand_tv.setTextColor(Color.GREEN)
        secondOperand_tv.setTextColor(Color.GREEN)
        operationSymbol_tv.setTextColor(Color.GREEN)
        gridLayout.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ok")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Show wrong in case of game lose
     * ---------------------------------------------------------------------------------------------
     */
    private fun showWrongResult() {
        instructions_tv.visibility = View.INVISIBLE
        result_tv.visibility = View.VISIBLE
        result_tv.text = resources.getString(R.string.wrong_str) + " : " + answerOK
        result_tv.setTextColor(Color.RED)
        firstOperand_tv.setTextColor(Color.RED)
        secondOperand_tv.setTextColor(Color.RED)
        operationSymbol_tv.setTextColor(Color.RED)
        gridLayout.visibility = View.INVISIBLE

        // statistics
        Results.incrementGameResultsThread("operations_executed")
        Results.incrementGameResultsThread("operations_ko")
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
        firstOperand_tv.setTextColor(quizDefaultTextColor)
        secondOperand_tv.setTextColor(quizDefaultTextColor)
        operationSymbol_tv.setTextColor(quizDefaultTextColor)
        instructions_tv.visibility = View.VISIBLE
        gridLayout.visibility = View.VISIBLE
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
        Results.updateGameResultHighscoreThread(scoreType, score)
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


    /**
     * ---------------------------------------------------------------------------------------------
     * Choose the right operands based on based on operation symbol,update UI, do calculation ,
     * and store the correct answer.
     * ---------------------------------------------------------------------------------------------
     */
    private fun calculateOperation() {
        when (operation) {
            '+' -> {
                // set operands to be processed
                firstOperand = MathBrainerUtility.randRange_ApiCheck(min, max)
                secondOperand = MathBrainerUtility.randRange_ApiCheck(min, max)

                // store correct answer
                answerOK = firstOperand + secondOperand

                // set operations value in view
                operationSymbol_tv.text = Character.toString(operation)

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
                operationSymbol_tv.text = Character.toString(operation)

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
                operationSymbol_tv.text = "X"

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
                operationSymbol_tv.text = Character.toString(operation)

                // statistics
                Results.incrementGameResultsThread("divisions")
            }
            else -> {
            }
        }


        firstOperand_tv.text = Integer.toString(firstOperand)
        secondOperand_tv.text = Integer.toString(secondOperand)

        // setup answers on button
        setupAnswersBtn()


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create setup correct answer and false answer on buttons
     * ---------------------------------------------------------------------------------------------
     */
    private fun setupAnswersBtn() {
        // choose the button where put the correct answer
        correctBtnNumber = MathBrainerUtility.randRange_ApiCheck(minAnswerBtnNum, maxAnswerBtnNum)
        var tmpBtn : Button? = getTheBtnNumber(correctBtnNumber)
        tmpBtn!!.text = Integer.toString(answerOK)

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


    /**
     * ---------------------------------------------------------------------------------------------
     * Set btn visible or not depending on current level button visible
     * @param thisBtn
     * ---------------------------------------------------------------------------------------------
     */
    private fun setAnswerBtnVisibility(thisBtn: Button) {

        val guess = MathBrainerUtility.randomSignChooser()
        if (guess > 0 && currentLevelAnswerBtnVisible > 0) {
            thisBtn.visibility = View.VISIBLE
            currentLevelAnswerBtnVisible--
        } else {
            thisBtn.visibility = View.INVISIBLE
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    private fun randomOffsetSum(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, (offset * 1.5).toInt())
        return if (result >= 1 && result <= 3) {
            answerOK + MathBrainerUtility.randomSignChooser() * result
        } else answerOK + result
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for multiplication generator
     * ---------------------------------------------------------------------------------------------
     */
    private fun randomOffsetMult(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, offset * 2)
        val sign = MathBrainerUtility.randomSignChooser()

        return if (result >= 4 && result <= 11) {
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


    /**
     * ---------------------------------------------------------------------------------------------
     * Return the button based on number
     * @param num
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    internal fun getTheBtnNumber(num: Int): Button? {
        when (num) {
            1 -> return answer01Btn
            2 -> return answer02Btn
            3 -> return answer03Btn
            4 -> return answer04Btn
            5 -> return answer05Btn
            6 -> return answer06Btn
            7 -> return answer07Btn
            8 -> return answer08Btn
            9 -> return answer09Btn
            else -> {
            }
        }
        return null
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show end game message
     * ---------------------------------------------------------------------------------------------
     */
    private fun endGame() {
        val handler = Handler()

        // reset counter
        countDownIndicator.countdownReset()

        // gridLayout.setVisibility(View.INVISIBLE);
        showWrongResult()

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
                this@Math_Op_Choose_Result_Activity, this)

        hideLastQuiz()

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide quiz
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideLastQuiz() {
        firstOperand_tv.visibility = View.INVISIBLE
        secondOperand_tv.visibility = View.INVISIBLE
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

        private val TAG = Math_Op_Choose_Result_Activity::class.java.simpleName
    }


}
