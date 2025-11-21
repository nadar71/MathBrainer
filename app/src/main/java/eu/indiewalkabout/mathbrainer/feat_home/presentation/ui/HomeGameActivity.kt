package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility
import eu.indiewalkabout.mathbrainer.core.util.TAG
import eu.indiewalkabout.mathbrainer.databinding.ActivityHomeGameBinding
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.OLD.GameResult
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.OLD.Results
import eu.indiewalkabout.mathbrainer.feat_credits.presentation.ui.GameCreditsActivity
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighScoresViewModel
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighscoresActivity
import eu.indiewalkabout.mathbrainer.feat_statistics.presentation.ui.HighscoresViewModelFactory
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui.DoubleNumberActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui.Math_Op_Choose_Result_Activity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui.Math_Op_Write_Result_Activity
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui.RandomOperationActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui.CountObjectsActivity
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui.NumberOrderActivity

// Choose the type of game
class HomeGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeGameBinding

    // highscores value
    private var totalHighScore_value: Int = 0
    private var sumWriteHighscore_value: Int = 0
    private var diffWriteHighscore_value: Int = 0
    private var multWriteHighscore_value: Int = 0
    private var divWriteHighscore_value: Int = 0
    private var sumChooseHighscore_value: Int = 0
    private var diffChooseHighscore_value: Int = 0
    private var multChooseHighscore_value: Int = 0
    private var divChooseHighscore_value: Int = 0
    private var doublingHighscore_value: Int = 0
    private var mixedOps_chooseHighscore_value: Int = 0
    private var mixedOps_writeHighscore_value: Int = 0
    private var randomOpsHighscore_value: Int = 0
    private var quickCountHighscore_value: Int = 0
    private var orderHighscore_value: Int = 0


    // game results list
    private var gameResults: List<GameResult>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_home_game)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_game)

        // init db with results to 0 where needed
        Results.initResultsThread()
        // keep scores to pass to games highscores visualization
        updateGamesScores()
        // activate all btn
        setBtnPressedListener()
        hideStatusNavBars()
    }

    override fun onStart() {
        super.onStart()
    }

    // Set up the button pressed listener and checking answers
    private fun setBtnPressedListener() {

        binding.sumChooseBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "+")
            intent.putExtra(HIGHSCORE, sumChooseHighscore_value)
            startActivity(intent)
        }

        binding.diffChooseBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "-")
            intent.putExtra(HIGHSCORE, diffChooseHighscore_value)
            startActivity(intent)
        }

        binding.multChooseBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "*")
            intent.putExtra(HIGHSCORE, multChooseHighscore_value)
            startActivity(intent)
        }

        binding.divChooseBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "/")
            intent.putExtra(HIGHSCORE, divChooseHighscore_value)
            startActivity(intent)
        }

        binding.sumWriteBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "+")
            intent.putExtra(HIGHSCORE, sumWriteHighscore_value)
            startActivity(intent)
        }

        binding.diffWriteBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "-")
            intent.putExtra(HIGHSCORE, diffWriteHighscore_value)
            startActivity(intent)
        }

        binding.multWriteBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "*")
            intent.putExtra(HIGHSCORE, multWriteHighscore_value)
            startActivity(intent)
        }

        binding.divWriteBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "/")
            intent.putExtra(HIGHSCORE, divWriteHighscore_value)
            startActivity(intent)
        }

        binding.resultWriteBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(HIGHSCORE, mixedOps_writeHighscore_value)
            startActivity(intent)
        }

        binding.resultChooseBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(HIGHSCORE, mixedOps_chooseHighscore_value)
            startActivity(intent)
        }

        binding.quickCountBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, CountObjectsActivity::class.java)
            intent.putExtra(HIGHSCORE, quickCountHighscore_value)
            startActivity(intent)
        }

        binding.doublingBtn.setOnClickListener {
            val intent = Intent(
                this@HomeGameActivity,
                DoubleNumberActivity::class.java
            )
            intent.putExtra(HIGHSCORE, doublingHighscore_value)
            startActivity(intent)
        }

        binding.orderBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, NumberOrderActivity::class.java)
            intent.putExtra(HIGHSCORE, orderHighscore_value)
            startActivity(intent)
        }

        binding.randomOpsBtn.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, RandomOperationActivity::class.java)
            intent.putExtra(HIGHSCORE, randomOpsHighscore_value)
            startActivity(intent)
        }

        binding.infoImg.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, GameCreditsActivity::class.java)
            startActivity(intent)
        }

        binding.highscoreImg.setOnClickListener {
            val intent = Intent(this@HomeGameActivity, HighscoresActivity::class.java)
            startActivity(intent)
        }
    }

    // Used to reload from db the tasks list and update the list view in screen
    private fun updateGamesScores() {
        val factory = HighscoresViewModelFactory()
        // keep updated with LiveData/ViewModel
        val viewModel = ViewModelProviders.of(this, factory).get(HighScoresViewModel::class.java)
        // retrieve data
        val gameResults = viewModel.gameResultsList
        // get data from observer, update view
        gameResults.observe(
            this,
            Observer { gameResultEntries ->
                Log.d(TAG, "LiveData : updated game results received. ")
                setGameResults(gameResultEntries)
            }
        )
    }

    // Put game result in textview
    private fun setGameResults(gameResults: List<GameResult>?) {
        this.gameResults = gameResults

        // set vars
        totalHighScore_value =
            MathBrainerUtility.getGameResultsFromList("global_score", gameResults!!)

        sumWriteHighscore_value =
            MathBrainerUtility.getGameResultsFromList("sum_write_result_game_score", gameResults)
        diffWriteHighscore_value =
            MathBrainerUtility.getGameResultsFromList("diff_write_result_game_score", gameResults)
        multWriteHighscore_value =
            MathBrainerUtility.getGameResultsFromList("mult_write_result_game_score", gameResults)
        divWriteHighscore_value =
            MathBrainerUtility.getGameResultsFromList("div_write_result_game_score", gameResults)

        sumChooseHighscore_value =
            MathBrainerUtility.getGameResultsFromList("sum_choose_result_game_score", gameResults)
        diffChooseHighscore_value =
            MathBrainerUtility.getGameResultsFromList("diff_choose_result_game_score", gameResults)
        multChooseHighscore_value =
            MathBrainerUtility.getGameResultsFromList("mult_choose_result_game_score", gameResults)
        divChooseHighscore_value =
            MathBrainerUtility.getGameResultsFromList("div_choose_result_game_score", gameResults)

        doublingHighscore_value =
            MathBrainerUtility.getGameResultsFromList("doublenumber_game_score", gameResults)
        mixedOps_chooseHighscore_value =
            MathBrainerUtility.getGameResultsFromList("mix_choose_result_game_score", gameResults)
        mixedOps_writeHighscore_value =
            MathBrainerUtility.getGameResultsFromList("mix_write_result_game_score", gameResults)
        randomOpsHighscore_value =
            MathBrainerUtility.getGameResultsFromList("random_op_game_score", gameResults)

        quickCountHighscore_value =
            MathBrainerUtility.getGameResultsFromList("count_objects_game_score", gameResults)
        orderHighscore_value =
            MathBrainerUtility.getGameResultsFromList("number_order_game_score", gameResults)
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

    companion object {
        val OPERATION_KEY = "operation"
        val HIGHSCORE = "highscore"
    }
}
