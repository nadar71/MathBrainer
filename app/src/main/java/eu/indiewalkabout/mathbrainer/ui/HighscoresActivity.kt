package eu.indiewalkabout.mathbrainer.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.google.android.gms.ads.AdView

import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.aritmetic.DoubleNumberActivity
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Choose_Result_Activity
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Write_Result_Activity
import eu.indiewalkabout.mathbrainer.aritmetic.RandomOperationActivity
import eu.indiewalkabout.mathbrainer.othergames.CountObjectsActivity
import eu.indiewalkabout.mathbrainer.othergames.NumberOrderActivity
import eu.indiewalkabout.mathbrainer.statistics.GameResult
import eu.indiewalkabout.mathbrainer.util.ConsentSDK
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility
import kotlinx.android.synthetic.main.activity_highscores.*

/**
 * ---------------------------------------------------------------------------------------------
 * Choose the type of game
 * ---------------------------------------------------------------------------------------------
 */
class HighscoresActivity : AppCompatActivity() {


    // admob banner ref
    private var mAdView: AdView? = null

    /*
    // Button refs
    internal var sumChooseBtn:  Button? = null
    internal var diffChooseBtn:  Button? = null
    internal var multChooseBtn:  Button? = null
    internal var divChooseBtn:  Button? = null

    internal var sumWriteBtn:  Button? = null
    internal var diffWriteBtn:  Button? = null
    internal var multWriteBtn:  Button? = null
    internal var divWriteBtn:  Button? = null

    internal var resultWriteBtn:  Button? = null
    internal var resultChooseBtn:  Button? = null
    internal var doublingBtn:  Button? = null

    internal var randomOpsBtn:  Button? = null

    internal var quickCountBtn:  ImageView? = null
    internal var orderBtn:  ImageView? = null
    internal var home_img:  ImageView? = null


    // game results scores ref
    internal var totalHighScore_tv:  TextView? = null
    internal var sumWriteScore_tv:  TextView? = null
    internal var diffWriteScore_tv:  TextView? = null
    internal var multWriteScore_tv:  TextView? = null
    internal var divWriteScore_tv:  TextView? = null
    internal var sumChooseScore_tv:  TextView? = null
    internal var diffChooseScore_tv:  TextView? = null
    internal var multChooseScore_tv:  TextView? = null
    internal var divChooseScore_tv:  TextView? = null
    internal var doublingScore_tv:  TextView? = null
    internal var mixedOps_chooseScore_tv:  TextView? = null
    internal var mixedOps_writeScore_tv:  TextView? = null
    internal var randomOpsScore_tv:  TextView? = null
    internal var quickCountScore_tv:  TextView? = null
    internal var orderScore_tv:  TextView? = null

     */

    // game results list
    internal var gameResults: List<GameResult>? = null

    // highscores value
    internal var totalHighScore_value: Int = 0
    internal var sumWriteHighscore_value: Int = 0
    internal var diffWriteHighscore_value: Int = 0
    internal var multWriteHighscore_value: Int = 0
    internal var divWriteHighscore_value: Int = 0
    internal var sumChooseHighscore_value: Int = 0
    internal var diffChooseHighscore_value: Int = 0
    internal var multChooseHighscore_value: Int = 0
    internal var divChooseHighscore_value: Int = 0
    internal var doublingHighscore_value: Int = 0
    internal var mixedOps_chooseHighscore_value: Int = 0
    internal var mixedOps_writeHighscore_value: Int = 0
    internal var randomOpsHighscore_value: Int = 0
    internal var quickCountHighscore_value: Int = 0
    internal var orderHighscore_value: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscores)


        // Initialize ConsentSDK
        val consentSDK = ConsentSDK.Builder(this)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build()


        // To check the consent and load ads
        consentSDK.checkConsent(object : ConsentSDK.ConsentCallback() {
            override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : $isRequestLocationInEeaOrUnknown")
                // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                mAdView!!.loadAd(ConsentSDK.getAdRequest(this@HighscoresActivity))
            }
        })

        mAdView = findViewById(R.id.adView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView!!.loadAd(ConsentSDK.getAdRequest(this@HighscoresActivity))

        /*
        // -----------------------------------------------------------------------------------------
        // Aritmetic operation button
        // -----------------------------------------------------------------------------------------

        // --------------------------------------------
        // Basic op btn
        // --------------------------------------------
        sumChooseBtn = findViewById(R.id.sumChooseBtn)
        diffChooseBtn = findViewById(R.id.diffChooseBtn)
        multChooseBtn = findViewById(R.id.multChooseBtn)
        divChooseBtn = findViewById(R.id.divChooseBtn)

        sumWriteBtn = findViewById(R.id.sumWriteBtn)
        diffWriteBtn = findViewById(R.id.diffWriteBtn)
        multWriteBtn = findViewById(R.id.multWriteBtn)
        divWriteBtn = findViewById(R.id.divWriteBtn)


        // --------------------------------------------
        // Mixed op btn
        // --------------------------------------------
        resultWriteBtn = findViewById(R.id.resultWriteBtn)
        resultChooseBtn = findViewById(R.id.resultChooseBtn)
        quickCountBtn = findViewById(R.id.quickCountBtn)
        doublingBtn = findViewById(R.id.doublingBtn)
        orderBtn = findViewById(R.id.orderBtn)
        randomOpsBtn = findViewById(R.id.randomOpsBtn)

        home_img = findViewById(R.id.home_img)
        */

        // --------------------------------------------
        // Games results ref
        // --------------------------------------------
        /*
        totalHighScore_tv = findViewById(R.id.totalHighScore_tv)
        sumWriteScore_tv = findViewById(R.id.sumWriteScore_tv)
        diffWriteScore_tv = findViewById(R.id.diffWriteScore_tv)
        multWriteScore_tv = findViewById(R.id.multWriteScore_tv)
        divWriteScore_tv = findViewById(R.id.divWriteScore_tv)
        sumChooseScore_tv = findViewById(R.id.sumChooseScore_tv)
        diffChooseScore_tv = findViewById(R.id.diffChooseScore_tv)
        multChooseScore_tv = findViewById(R.id.multChooseScore_tv)
        divChooseScore_tv = findViewById(R.id.divChooseScore_tv)
        doublingScore_tv = findViewById(R.id.doublingScore_tv)
        mixedOps_chooseScore_tv = findViewById(R.id.mixedOps_chooseScore_tv)
        mixedOps_writeScore_tv = findViewById(R.id.mixedOps_writeScore_tv)
        randomOpsScore_tv = findViewById(R.id.randomOpsScore_tv)
        quickCountScore_tv = findViewById(R.id.quickCountScore_tv)
        orderScore_tv = findViewById(R.id.orderScore_tv)
        */



        // show updated scores
        showGamesScores()


        // activate clicks on answer buttons
        setBtnPressedListener()

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set up the button pressed listener and checking answers
     * ---------------------------------------------------------------------------------------------
     */
    private fun setBtnPressedListener() {

        sumChooseBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "+")
            intent.putExtra(HIGHSCORE, sumChooseHighscore_value)
            startActivity(intent)
        }

        diffChooseBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "-")
            intent.putExtra(HIGHSCORE, diffChooseHighscore_value)
            startActivity(intent)
        }


        multChooseBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "*")
            intent.putExtra(HIGHSCORE, multChooseHighscore_value)
            startActivity(intent)
        }


        divChooseBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "/")
            intent.putExtra(HIGHSCORE, divChooseHighscore_value)
            startActivity(intent)
        }

        sumWriteBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "+")
            intent.putExtra(HIGHSCORE, sumWriteHighscore_value)
            startActivity(intent)
        }


        diffWriteBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "-")
            intent.putExtra(HIGHSCORE, diffWriteHighscore_value)
            startActivity(intent)
        }


        multWriteBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "*")
            intent.putExtra(HIGHSCORE, multWriteHighscore_value)
            startActivity(intent)
        }

        divWriteBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(OPERATION_KEY, "/")
            intent.putExtra(HIGHSCORE, divWriteHighscore_value)
            startActivity(intent)
        }


        resultWriteBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Write_Result_Activity::class.java)
            intent.putExtra(HIGHSCORE, mixedOps_writeHighscore_value)
            startActivity(intent)
        }

        resultChooseBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, Math_Op_Choose_Result_Activity::class.java)
            intent.putExtra(HIGHSCORE, mixedOps_chooseHighscore_value)
            startActivity(intent)
        }

        quickCountBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, CountObjectsActivity::class.java)
            intent.putExtra(HIGHSCORE, quickCountHighscore_value)
            startActivity(intent)
        }


        doublingBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, DoubleNumberActivity::class.java)
            intent.putExtra(HIGHSCORE, doublingHighscore_value)
            startActivity(intent)
        }


        orderBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, NumberOrderActivity::class.java)
            intent.putExtra(HIGHSCORE, orderHighscore_value)
            startActivity(intent)
        }

        randomOpsBtn.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, RandomOperationActivity::class.java)
            intent.putExtra(HIGHSCORE, randomOpsHighscore_value)
            startActivity(intent)
        }


        home_img.setOnClickListener {
            val intent = Intent(this@HighscoresActivity, ChooseGameActivity::class.java)
            startActivity(intent)
        }

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private fun showGamesScores() {
        val factory = HighscoresViewModelFactory()

        // keep updated with LiveData/ViewModel
        val viewModel = ViewModelProviders.of(this, factory).get(HighScoresViewModel::class.java)

        // retrieve data
        val gameResults = viewModel.gameResultsList

        // get data from observer, update view
        gameResults.observe(this, Observer { gameResultEntries ->
            Log.d(TAG, "LiveData : updated game results received. ")
            setGameResults(gameResultEntries)
        })
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Put game result in textview
     * ---------------------------------------------------------------------------------------------
     */
    private fun setGameResults(gameResults: List<GameResult>?) {

        this.gameResults = gameResults

        // set vars
        totalHighScore_value = MathBrainerUtility.getGameResultsFromList("global_score", gameResults!!)

        sumWriteHighscore_value = MathBrainerUtility.getGameResultsFromList("sum_write_result_game_score", gameResults)
        diffWriteHighscore_value = MathBrainerUtility.getGameResultsFromList("diff_write_result_game_score", gameResults)
        multWriteHighscore_value = MathBrainerUtility.getGameResultsFromList("mult_write_result_game_score", gameResults)
        divWriteHighscore_value = MathBrainerUtility.getGameResultsFromList("div_write_result_game_score", gameResults)

        sumChooseHighscore_value = MathBrainerUtility.getGameResultsFromList("sum_choose_result_game_score", gameResults)
        diffChooseHighscore_value = MathBrainerUtility.getGameResultsFromList("diff_choose_result_game_score", gameResults)
        multChooseHighscore_value = MathBrainerUtility.getGameResultsFromList("mult_choose_result_game_score", gameResults)
        divChooseHighscore_value = MathBrainerUtility.getGameResultsFromList("div_choose_result_game_score", gameResults)

        doublingHighscore_value = MathBrainerUtility.getGameResultsFromList("doublenumber_game_score", gameResults)
        mixedOps_chooseHighscore_value = MathBrainerUtility.getGameResultsFromList("mix_choose_result_game_score", gameResults)
        mixedOps_writeHighscore_value = MathBrainerUtility.getGameResultsFromList("mix_write_result_game_score", gameResults)
        randomOpsHighscore_value = MathBrainerUtility.getGameResultsFromList("random_op_game_score", gameResults)

        quickCountHighscore_value = MathBrainerUtility.getGameResultsFromList("count_objects_game_score", gameResults)
        orderHighscore_value = MathBrainerUtility.getGameResultsFromList("number_order_game_score", gameResults)


        // set textviews
        totalHighScore_tv.text = Integer.toString(totalHighScore_value)

        sumWriteScore_tv.text = Integer.toString(sumWriteHighscore_value)
        diffWriteScore_tv.text = Integer.toString(diffWriteHighscore_value)
        multWriteScore_tv.text = Integer.toString(multWriteHighscore_value)
        divWriteScore_tv.text = Integer.toString(divWriteHighscore_value)

        sumChooseScore_tv.text = Integer.toString(sumChooseHighscore_value)
        diffChooseScore_tv.text = Integer.toString(diffChooseHighscore_value)
        multChooseScore_tv.text = Integer.toString(multChooseHighscore_value)
        divChooseScore_tv.text = Integer.toString(divChooseHighscore_value)

        doublingScore_tv.text = Integer.toString(doublingHighscore_value)
        mixedOps_chooseScore_tv.text = Integer.toString(mixedOps_chooseHighscore_value)
        mixedOps_writeScore_tv.text = Integer.toString(mixedOps_writeHighscore_value)
        randomOpsScore_tv.text = Integer.toString(randomOpsHighscore_value)

        quickCountScore_tv.text = Integer.toString(quickCountHighscore_value)
        orderScore_tv.text = Integer.toString(orderHighscore_value)

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

    companion object {
        private val TAG = HighscoresActivity::class.java.simpleName
        val OPERATION_KEY = "operation"
        val HIGHSCORE = "highscore"
    }


}
