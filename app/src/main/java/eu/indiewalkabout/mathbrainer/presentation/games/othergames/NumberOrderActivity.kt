package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.core.util.GameOverDialog
import eu.indiewalkabout.mathbrainer.core.util.IGameFunctions
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityNumbersOrderBinding
import eu.indiewalkabout.mathbrainer.domain.model.results.Results
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.MarkerWithNoNumberView
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.MarkerWithNumberView
import eu.indiewalkabout.mathbrainer.presentation.games.customviews.SolutionsView
import eu.indiewalkabout.mathbrainer.presentation.ui.ChooseGameActivity
import java.util.ArrayList

class NumberOrderActivity : AppCompatActivity(), IGameFunctions {
    private lateinit var binding: ActivityNumbersOrderBinding
    private val unityAdsListener = UnityAdsListener()

    // Custom views drawing items to count
    private lateinit var drawquiz_challenge: MarkerWithNoNumberView
    private lateinit var drawquiz: MarkerWithNumberView
    private lateinit var solutionsView: SolutionsView

    private lateinit var livesValueIv: ArrayList<ImageView>

    // starting level
    private var level = 0

    // lives counter; 0 to gameover
    private var lives = 3

    // random range of offset from correct answer
    private val min = 1
    private val max = 6

    // store wring answer to avoid duplicates
    private lateinit var wrongAnswer: ArrayList<Int>

    // num of challenge to pass to next level
    // changing while level growing
    private var numChallengeEachLevel = 2
    private var countChallenge = 1

    // score var
    private var score = 0

    // Context
    private var context: Context? = null

    // max time to show the items to count
    private var timerLength = 2000f

    // max num range items to count for challenge
    private var maxItemsToCount = 4f

    // items to count in current level
    private var itemsToCount = maxItemsToCount.toInt()

    // get the touch event from custom class on markers touching
    private lateinit var touchEventResInCustomView: MutableLiveData<Int>

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
    override val isGameOver: Boolean
        get() {
            Log.d(TAG, "isGameOver: $lives")
            lives--
            Results.incrementGameResultsThread("lives_missed")
            if (lives > -1) {
                livesValueIv[lives].visibility = View.INVISIBLE
            }
            if (lives <= 0) {
                endGame()
                Results.incrementGameResultsThread("games_played")
                Results.incrementGameResultsThread("games_lose")
                Results.updateGameResultHighscoreThread("number_order_game_score", score)
                Results.incrementGameResultByDeltaThread("global_score", score)
                return true
            }
            return false
        }

    override fun checkPlayerInput() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_numbers_order)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_numbers_order)

        // Unity ads init
        UnityAds.initialize(this, resources.getString(R.string.unityads_key), unityAdsListener)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.mAdView.loadAd(ConsentSDK.getAdRequest(this@NumberOrderActivity))

        // setup context
        context = this

        // get the items to count view, set invisible at the moment
        solutionsView = findViewById(R.id.solutionsShowing_v)
        drawquiz_challenge = findViewById(R.id.itemDrawingNoNumber_v)
        drawquiz = findViewById(R.id.itemDrawing_v)

        // set quiz with and without number invisible, not already in the game
        drawquiz.visibility = View.INVISIBLE
        drawquiz_challenge.visibility = View.INVISIBLE

        // link list img with number with that with No number in the 2 separate view
        drawquiz_challenge.setImgNumberList(drawquiz.imgwithNUmberList)

        // put the SolutionView instance into drawquiz_challenge to draw solutions
        drawquiz_challenge.setSolutionView(solutionsView)

        binding.btnNewGame.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.INVISIBLE

        // init lives led images
        livesValueIv = ArrayList()
        livesValueIv.add(findViewById<View>(R.id.life_01_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_02_iv) as ImageView)
        livesValueIv.add(findViewById<View>(R.id.life_03_iv) as ImageView)

        // define wrong answers storage
        wrongAnswer = ArrayList()

        // newChallenge();

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
        // essential to show another quiz without loosing lives in case of onPause
        restartAfterPause(1)
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
        binding.btnNewGame.setOnClickListener { newChallenge() }

        binding.backhomeImg.setOnClickListener {
            // saves score
            isComingHome()

            // show unityads randomic
            MathBrainerUtility.showUnityAdsRandom(this@NumberOrderActivity)

            val intent = Intent(this@NumberOrderActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }
    }

    // Check if player input is right/wrong and update score
    private fun checkPlayerResult(result: Int) {
        // disable touch for the markers
        drawquiz_challenge._isTouchMarkersEnable = false
        // hide markers with and without numbers
        hideAll()

        // check if result is ok...
        if (result == 1) {
            updateScore()
            countChallenge++
            // rise level after numChallengeEachLevel reached
            if (countChallenge > numChallengeEachLevel) {
                // reset counter
                countChallenge = 0
                updateLevel()
            }
            showNewBtnAfterTimerLength(500, true)
            // ...otherwise a life will be lost
        } else if (result == 0) {
            // hideAnswerAfterTimerLength(1000);
            // lose a life, check if it's game over
            val gameover = isGameOver

            // new number to double
            if (gameover == false) {
                showNewBtnAfterTimerLength(500, false)
            }
        }
    }

    // Show button when exiting pause state
    private fun restartAfterPause(timerLength: Int) {
        val handler = Handler()
        hideAll()
        val runnable = Runnable {
            // it isn't show, but is essential for onPause/onResume sequence,
            // to show another quiz without loosing life
            binding.resultTv.text = resources.getString(R.string.quick_count_relaunch)
            binding.resultTv.setTextColor(Color.GREEN)
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }

    // Show button for new challenge after timerLength
    private fun showNewBtnAfterTimerLength(timerLength: Int, win: Boolean) {
        val handler = Handler()
        val runnable = Runnable {
            binding.btnNewGame.visibility = View.VISIBLE
            binding.quizInstructionsTv.text = "   "
            binding.resultTv.visibility = View.VISIBLE
            if (win == false) {
                binding.resultTv.text = resources.getString(R.string.wrong_str)
                binding.resultTv.setTextColor(Color.RED)

                // statistics
                Results.incrementGameResultsThread("operations_executed")
                Results.incrementGameResultsThread("operations_ko")
            } else if (win == true) {
                binding.resultTv.text = resources.getString(R.string.ok_str)
                binding.resultTv.setTextColor(Color.GREEN)
                // statistics
                Results.incrementGameResultsThread("operations_executed")
                Results.incrementGameResultsThread("operations_ok")
                Results.incrementGameResultByDeltaThread("numbers_in_order", itemsToCount)
            } else if (!win) {
                binding.resultTv.text = resources.getString(R.string.quick_count_relaunch)
                binding.resultTv.setTextColor(Color.GREEN)
            }
        }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
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
        Results.updateGameResultHighscoreThread("number_order_game_score", score)
        Results.incrementGameResultByDeltaThread("global_score", score)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        super.addContentView(view, params)
    }

    override fun updateProgressBar(progress: Int) {
    }

    // Set new challenge in view
    override fun newChallenge() {
        // clear wrong answers list
        wrongAnswer.clear()

        // reset previous game
        drawquiz_challenge.resetGame()

        // show the items in number defined by level
        itemsToCount = MathBrainerUtility.randRange_ApiCheck(Math.ceil(maxItemsToCount * 0.7).toInt(), maxItemsToCount.toInt())
        drawquiz.redraw(itemsToCount)
        drawquiz_challenge.redraw(itemsToCount)

        // show items to count
        showQuiz()

        // hide and show button for answer
        hideQuizAfterTimerLength(timerLength.toInt())

        Log.d(TAG, "newChallenge: $countChallenge")

        touchEventResInCustomView = drawquiz_challenge.touchResult
        touchEventResInCustomView.observe(
            this,
            Observer {
                val eventValue = touchEventResInCustomView.value
                // conditional necessary to exclude changes to -1
                if (eventValue == 1) {
                    checkPlayerResult(eventValue)
                } else if (eventValue == 0) {
                    drawquiz_challenge.setTouchResult(-1)
                    checkPlayerResult(eventValue)
                }
            }
        )
    }

    // Show markers' order with number
    private fun showQuiz() {
        binding.quizInstructionsTv.text = resources.getString(R.string.click_order_instructions)
        solutionsView.visibility = View.INVISIBLE
        binding.btnNewGame.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.INVISIBLE
        drawquiz.visibility = View.VISIBLE
    }

    // Show markers to touch with no numbers
    private fun hideQuiz() {
        // debug drawquiz.setVisibility(View.INVISIBLE);
        binding.quizInstructionsTv.text = resources.getString(R.string.click_order_start)
        solutionsView.visibility = View.VISIBLE
        drawquiz_challenge.visibility = View.VISIBLE
    }

    // Hide all
    private fun hideAll() {
        drawquiz.visibility = View.INVISIBLE
        drawquiz_challenge.visibility = View.INVISIBLE
        solutionsView.visibility = View.INVISIBLE
    }

    // Hide items group after timerLength
    private fun hideQuizAfterTimerLength(timerLength: Int) {
        val handler = Handler()
        val runnable = Runnable { hideQuiz() }
        // execute runnable after a timerLength
        handler.postDelayed(runnable, timerLength.toLong())
    }

    // Show game over with delay
    private fun endGame() {
        binding.btnNewGame.visibility = View.INVISIBLE
        binding.resultTv.visibility = View.VISIBLE
        binding.resultTv.text = resources.getString(R.string.wrong_str)
        binding.resultTv.setTextColor(Color.RED)
        val handler = Handler()
        val runnable = Runnable { showGameOverDialog() }
        handler.postDelayed(runnable, 500)
    }

    // Show gameover dialog
    private fun showGameOverDialog() {
        gameOverDialog = GameOverDialog(
            this,
            this@NumberOrderActivity, this
        )
        binding.resultTv.visibility = View.INVISIBLE
        binding.quizInstructionsTv.visibility = View.INVISIBLE
        drawquiz.visibility = View.INVISIBLE
        drawquiz_challenge.visibility = View.INVISIBLE
    }

    // Updating level
    private fun updateLevel() {
        // increment level
        level++
        binding.levelValueTv.text = level.toString()
        // increment level difficulty
        if (level in 2..29) {
            maxItemsToCount += 0.5f
            timerLength += 0.5f
            numChallengeEachLevel += 1
            Log.d(
                TAG,
                "updatingLevel: New Level! new min : " + min + " new max: " +
                    max + " new level : " + level + " Timer now at : " + timerLength / 1000 + " sec."
            )
        }
        // statistics
        Results.incrementGameResultsThread("level_upgrades")
    }

    // Check state at countdown expired
    override fun checkCountdownExpired() {
        // lose a life, check if it's game over
        val gameover = isGameOver
        // new number to double
        if (!gameover) {
            // show result and start a new game session if allowed
            showNewBtnAfterTimerLength(500, false)
        }
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
        // saves score
        isComingHome()
        // show unityads randomic
        MathBrainerUtility.showUnityAdsRandom(this)
    }
}
