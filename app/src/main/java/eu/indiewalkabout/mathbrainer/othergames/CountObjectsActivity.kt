package eu.indiewalkabout.mathbrainer.othergames

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import java.io.IOException
import java.util.ArrayList

import eu.indiewalkabout.mathbrainer.ui.ChooseGameActivity
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.statistics.Results
import eu.indiewalkabout.mathbrainer.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.util.EndGameSessionDialog
import eu.indiewalkabout.mathbrainer.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility


import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.customviews.QuickCountItemDrawView

import kotlinx.android.synthetic.main.activity_count_objects.*
import kotlinx.android.synthetic.main.activity_count_objects.backhome_img
import kotlinx.android.synthetic.main.activity_count_objects.gridLayout
import kotlinx.android.synthetic.main.activity_count_objects.highscore_label_tv
import kotlinx.android.synthetic.main.activity_count_objects.highscore_value_tv
import kotlinx.android.synthetic.main.activity_count_objects.levelValue_tv
import kotlinx.android.synthetic.main.activity_count_objects.result_tv
import kotlinx.android.synthetic.main.activity_count_objects.scoreLabel_tv
import kotlinx.android.synthetic.main.activity_count_objects.scoreValue_tv
import kotlinx.android.synthetic.main.activity_math_op_choose_result.mAdView
import kotlinx.android.synthetic.main.activity_math_op_choose_result.*

class CountObjectsActivity : AppCompatActivity(), IGameFunctions {

    private val unityAdsListener = UnityAdsListener()

    private lateinit var lifesValue_iv: ArrayList<ImageView>

    // answer and its stuff
    private var answerOK: Int = 0
    private var correctBtnNumber = 1
    private val offset = 10
    private var pressedBtnValue = 0

    // starting level
    private var level = 0

    // lifes counter; 0 to gameover
    private var lifes = 3

    // random range of offset from correct answer
    private val min = 1
    private val max = 6

    // Our costumview img references (almost useless)
    private lateinit var ourFrame: ImageView

    // Costum view drawing items to count
    private lateinit var drawquiz: QuickCountItemDrawView

    // store wring answer to avoid duplicates
    internal lateinit var wrongAnswer: ArrayList<Int>

    // num of challenge to pass to next level
    // changing while level growing
    private var numChallengeEachLevel = 1
    private var countChallenge = 1

    // random range for answer btn number
    // changing while level growing
    private val minAnswerBtnNum = 1
    private val maxAnswerBtnNum = 4

    // score var
    private var score = 0

    // Context
    private var context: Context? = null

    // max time to show the items to count
    private var timerLength = 2000f

    // max num range items to count for challenge
    private var maxItemsToCount = 6

    // items to count in current level
    private var itemsToCount = maxItemsToCount

    // game session end dialog
    internal var endSessiondialog: EndGameSessionDialog? = null

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
    val isGameOver: Boolean
        get() {
            Log.d(TAG, "isGameOver: $lifes")
            lifes--
            Results.incrementGameResultsThread("lifes_missed")
            if (lifes > -1) {
                lifesValue_iv[lifes].visibility = View.INVISIBLE
            }
            if (lifes <= 0) {

                endGame()
                Results.incrementGameResultsThread("games_played")
                Results.incrementGameResultsThread("games_lose")
                Results.updateGameResultHighscoreThread("count_objects_game_score", score)
                Results.incrementGameResultByDeltaThread("global_score", score)

                return true

            }

            return false

        }


    // ---------------------------------------------------------------------------------------------
    //                                    UNUSED STUFF, just for debug
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // UNUSED , just for debug
    // Get the width of the screen
    // ---------------------------------------------------------------------------------------------
    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    // ---------------------------------------------------------------------------------------------
    // UNUSED , just for debug
    // Get the height of the screen
    // ---------------------------------------------------------------------------------------------
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_count_objects)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(this@CountObjectsActivity))

        // setup context
        context = this

        //Get a reference to our ImageView in the layout
        ourFrame = findViewById<View>(R.id.canvas_image_ref_img) as ImageView

        // get the items to count view, set inviisible at the moment
        drawquiz = findViewById(R.id.itemDrawing_v)

        // get the items to count view, set inviisible at the moment
        drawquiz.visibility = View.INVISIBLE

        btnNewGame.visibility = View.INVISIBLE
        result_tv.visibility = View.INVISIBLE

        // init lifes led images
        lifesValue_iv = ArrayList()
        lifesValue_iv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        lifesValue_iv.add(findViewById<View>(R.id.life_03_iv) as ImageView)

        // define wrong answers storage
        wrongAnswer = ArrayList()


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
        answer_01_btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer_02_btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer_03_btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }


        answer_04_btn.setOnClickListener { view ->
            val b = view as Button
            pressedBtnValue = Integer.parseInt(b.text as String)
            checkPlayerInput()
        }

        btnNewGame.setOnClickListener { newChallenge() }

        backhome_img.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@CountObjectsActivity)

            val intent = Intent(this@CountObjectsActivity, ChooseGameActivity::class.java)
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
        if (pressedBtnValue != 0 && pressedBtnValue == answerOK) {

            updateScore()

            countChallenge++

            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel) {
                // reset counter
                countChallenge = 0

                updateLevel()
            }

            showNewBtnAfterTimerLength(100, true)

            // ...otherwise a life will be lost

        } else {

            // hideAnswerAfterTimerLength(1000);
            // lose a life, check if it's game over
            val gameover = isGameOver

            if (gameover == false) {
                showNewBtnAfterTimerLength(100, false)
            }

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show button for new challenge after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private fun showNewBtnAfterTimerLength(timerLength: Int, win: Boolean) {
        gridLayout.visibility = View.INVISIBLE
        quiz_instructions_tv.visibility = View.INVISIBLE

        val handler = Handler()
        val runnable = Runnable {
            btnNewGame.visibility = View.VISIBLE
            quiz_instructions_tv.text = "   "
            result_tv.visibility = View.VISIBLE
            if (win == false) {
                result_tv.text = resources.getString(R.string.wrong_str)
                result_tv.setTextColor(Color.RED)

                // statistics
                Results.incrementGameResultsThread("operations_executed")
                Results.incrementGameResultsThread("operations_ko")

            } else if (win == true) {
                result_tv.text = resources.getString(R.string.ok_str)
                result_tv.setTextColor(Color.GREEN)

                // statistics
                Results.incrementGameResultsThread("operations_executed")
                Results.incrementGameResultsThread("operations_ok")
                Results.incrementGameResultByDeltaThread("objects_counted", itemsToCount)
            }
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide answer as items group after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideAnswerAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable { drawquiz.visibility = View.INVISIBLE }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Launch new challenge after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private fun newchallengeAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable {
            gridLayout.visibility = View.INVISIBLE
            quiz_instructions_tv.visibility = View.INVISIBLE
            btnNewGame.visibility = View.VISIBLE
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
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
        Results.updateGameResultHighscoreThread("count_objects_game_score", score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        super.addContentView(view, params)
    }

    override fun updateProgressBar(progress: Int) {

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set new challenge in view
     * ---------------------------------------------------------------------------------------------
     */
    override fun newChallenge() {
        // clear wrong answers list
        wrongAnswer.clear()

        result_tv.visibility = View.INVISIBLE

        // show the items in number defined by level
        itemsToCount = MathBrainerUtility.randRange_ApiCheck(Math.ceil(maxItemsToCount * 0.7).toInt(), maxItemsToCount)
        drawquiz.redraw(itemsToCount)

        // show items to count
        showItems()

        // hide and show button for answer
        hideQuizAfterTimerLength(timerLength.toInt())

        Log.d(TAG, "newChallenge: $countChallenge")

        // set the answer buttons
        setupAnswersBtn(itemsToCount)

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private fun showItems() {
        btnNewGame.visibility = View.INVISIBLE
        gridLayout.visibility = View.INVISIBLE
        quiz_instructions_tv.visibility = View.INVISIBLE
        drawquiz.visibility = View.VISIBLE
        count_obj_instructions_tv.visibility = View.VISIBLE
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show items to count, hide anserws buttons
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideItems() {
        gridLayout.visibility = View.VISIBLE
        quiz_instructions_tv.visibility = View.VISIBLE
        quiz_instructions_tv.text = resources.getString(R.string.count_objects_question)
        drawquiz.visibility = View.INVISIBLE
        count_obj_instructions_tv.visibility = View.INVISIBLE
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Hide items group after timerLength
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideQuizAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable { hideItems() }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create setup correct answer and false answer on buttons
     * ---------------------------------------------------------------------------------------------
     */
    private fun setupAnswersBtn(numItemsToCount: Int) {

        // set the correct answer
        answerOK = itemsToCount

        // choose the button where put the correct answer
        correctBtnNumber = MathBrainerUtility.randRange_ApiCheck(minAnswerBtnNum, maxAnswerBtnNum)
        var tmpBtn = getTheBtnNumber(correctBtnNumber)
        tmpBtn!!.text = Integer.toString(answerOK)


        // set wrong answer on the others
        for (i in 1..maxAnswerBtnNum) {
            if (i != correctBtnNumber) {

                tmpBtn = getTheBtnNumber(i)
                var result = 0
                do {
                    result = randomOffsetSum()
                } while (wrongAnswer.lastIndexOf(result) > 0)
                wrongAnswer.add(result)

                tmpBtn!!.text = result.toString()

            } else { // the btn with the right answer must be alwys visible
                tmpBtn = getTheBtnNumber(correctBtnNumber)
            }
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Random answer for sum generator
     * ---------------------------------------------------------------------------------------------
     */
    private fun randomOffsetSum(): Int {
        val result = MathBrainerUtility.randRange_ApiCheck(1, (offset * 1.0).toInt())
        if (result >= 1 && result <= 3) {
            val sign = MathBrainerUtility.randomSignChooser()
            return answerOK + sign * result
        }
        return answerOK + result
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
            1 -> return answer_01_btn
            2 -> return answer_02_btn
            3 -> return answer_03_btn
            4 -> return answer_04_btn
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

        result_tv.visibility = View.VISIBLE
        result_tv.text = resources.getString(R.string.wrong_str)
        result_tv.setTextColor(Color.RED)

        gridLayout.visibility = View.INVISIBLE
        quiz_instructions_tv.visibility = View.INVISIBLE
        drawquiz.visibility = View.INVISIBLE

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
                this@CountObjectsActivity, this)

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
        if (level > 1 && level < 30) {

            maxItemsToCount += 2

            timerLength += 0.5f

            numChallengeEachLevel += 0

            Log.d(TAG, "updatingLevel: New Level! new min : " + min + " new max: "
                    + max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec.")
        }

        // statistics
        Results.incrementGameResultsThread("level_upgrades")
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Check state at countdown expired
     * ---------------------------------------------------------------------------------------------
     */
    override fun checkCountdownExpired() {

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

        // saves score
        isComingHome()

        // show unityads randomic
        MathBrainerUtility.showUnityAdsRandom(this)

    }


    // -------------------------------------------
    // UNUSED , just for debug
    // Draw game items to count
    // -------------------------------------------
    private fun drawGame() {

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenWidth = size.x
        val screenHeight = size.y

        val validDrawAreaWidth = (screenWidth * 1.05).toInt()
        val validDrawAreaHeight = (screenHeight * 0.8).toInt()

        // int validDrawAreaItemsWidth  = (int) (screenWidth  * 0.5);
        // int validDrawAreaItemsHeight = (int) (screenHeight * 0.5);

        val validDrawAreaItemsWidth = (screenWidth * 0.95).toInt()
        val validDrawAreaItemsHeight = (screenHeight * 0.72).toInt()

        val imageScaleXY = 0.2f

        //Create a bitmap object to use as our canvas
        Log.d(TAG, "onCreate: validDrawAreaItemsWidth : $validDrawAreaItemsWidth validDrawAreaItemsHeight : $validDrawAreaItemsHeight")

        val ourBitmap = Bitmap.createBitmap(validDrawAreaWidth, validDrawAreaHeight, Bitmap.Config.ARGB_8888)

        //Assign the bitmap to image = our frame
        ourFrame.setImageBitmap(ourBitmap)

        // Assign the bitmap to the canvas
        val ourCanvas = Canvas(ourBitmap)

        //A paint object that does our drawing, on our canvas
        val paint = Paint()

        //Set the background color
        ourCanvas.drawColor(Color.TRANSPARENT)
        // ourCanvas.drawColor(Color.argb(0, 0, 0, 255));

        //Change the color of the virtual paint brush
        paint.color = Color.argb(255, 1, 255, 255)


        // draw 20 images
        for (i in 0..19) {
            try {
                // get images
                val assetManager = context!!.assets
                val inputStream = assetManager.open("memo" + (100 + i) + ".png")
                var item = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                // Log.d("BitmapText","memo" + (100+i) + ".png format: " + item.getConfig());

                // draw on canvas
                item = item.copy(Bitmap.Config.ARGB_8888, true)
                val randX = MathBrainerUtility.randRange_ApiCheck(15, validDrawAreaItemsWidth)
                val randY = MathBrainerUtility.randRange_ApiCheck(15, validDrawAreaItemsHeight)
                ourCanvas.drawBitmap(MathBrainerUtility.resizeBitmapByScale(item, imageScaleXY),
                        randX.toFloat(), randY.toFloat(), paint)

            } catch (e: IOException) {
                Log.d(TAG, "drawGame: " + e.message)
            } finally {
                // we should really close our input streams here.
            }
        }


        /*
        // dummy check from drawable
        Bitmap a = BitmapFactory.decodeResource(getResources(), R.drawable.memo101);
        a = a.copy(Bitmap.Config.ARGB_8888, true);
        int randX = MathBrainerUtility.randRange_ApiCheck(15, validDrawAreaItemsWidth);
        int randY = MathBrainerUtility.randRange_ApiCheck(15, validDrawAreaItemsHeight);
        ourCanvas.drawBitmap(MathBrainerUtility.resizeBitmapByScale(a, imageScaleXY, false, 100, 100), randX, randY, paint);
        */

        // invalidate view for redrawing
        ourFrame.invalidate()
    }

    companion object {

        private val TAG = CountObjectsActivity::class.java.simpleName
    }


}
