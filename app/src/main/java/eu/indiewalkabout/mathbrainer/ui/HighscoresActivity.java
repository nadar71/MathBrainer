package eu.indiewalkabout.mathbrainer.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.util.List;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.aritmetic.DoubleNumberActivity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.RandomOperationActivity;
import eu.indiewalkabout.mathbrainer.othergames.CountObjectsActivity;
import eu.indiewalkabout.mathbrainer.othergames.NumberOrderActivity;
import eu.indiewalkabout.mathbrainer.statistics.GameResult;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility;

/**
 * ---------------------------------------------------------------------------------------------
 * Choose the type of game
 * ---------------------------------------------------------------------------------------------
 */
public class HighscoresActivity extends AppCompatActivity {

    private static final String TAG = HighscoresActivity.class.getSimpleName();


    // admob banner ref
    private AdView mAdView;

    public static final String OPERATION_KEY = "operation";
    public static final String HIGHSCORE = "highscore";

    // Button refs
    Button sumChooseBtn;
    Button diffChooseBtn;
    Button multChooseBtn;
    Button divChooseBtn;

    Button sumWriteBtn;
    Button diffWriteBtn;
    Button multWriteBtn;
    Button divWriteBtn;

    Button resultWriteBtn;
    Button resultChooseBtn;
    Button doublingBtn;

    Button randomOpsBtn;

    ImageView quickCountBtn;
    ImageView orderBtn;
    ImageView home_img;


    // game results scores ref
    TextView totalHighScore_tv;
    TextView sumWriteScore_tv;
    TextView diffWriteScore_tv;
    TextView multWriteScore_tv;
    TextView divWriteScore_tv;
    TextView sumChooseScore_tv;
    TextView diffChooseScore_tv;
    TextView multChooseScore_tv;
    TextView divChooseScore_tv;
    TextView doublingScore_tv;
    TextView mixedOps_chooseScore_tv;
    TextView mixedOps_writeScore_tv;
    TextView randomOpsScore_tv;
    TextView quickCountScore_tv;
    TextView orderScore_tv;

    // game results list
    List<GameResult> gameResults;

    // highscores value
    int totalHighScore_value;
    int sumWriteHighscore_value;
    int diffWriteHighscore_value;
    int multWriteHighscore_value;
    int divWriteHighscore_value;
    int sumChooseHighscore_value;
    int diffChooseHighscore_value;
    int multChooseHighscore_value;
    int divChooseHighscore_value;
    int doublingHighscore_value;
    int mixedOps_chooseHighscore_value;
    int mixedOps_writeHighscore_value;
    int randomOpsHighscore_value;
    int quickCountHighscore_value;
    int orderHighscore_value;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);


        // Initialize ConsentSDK
        ConsentSDK consentSDK = new ConsentSDK.Builder(this)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build();


        // To check the consent and load ads
        consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
            @Override
            public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : "+isRequestLocationInEeaOrUnknown);
                // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                mAdView.loadAd(ConsentSDK.getAdRequest(HighscoresActivity.this));
            }
        });

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(HighscoresActivity.this));

        // -----------------------------------------------------------------------------------------
        // Aritmetic operation button
        // -----------------------------------------------------------------------------------------

        // --------------------------------------------
        // Basic op btn
        // --------------------------------------------
        sumChooseBtn    = findViewById(R.id.sumChoose_Btn);
        diffChooseBtn   = findViewById(R.id.diffChoose_Btn);
        multChooseBtn   = findViewById(R.id.multChoose_Btn);
        divChooseBtn    = findViewById(R.id.divChoose_Btn);

        sumWriteBtn     = findViewById(R.id.sumWrite_Btn);
        diffWriteBtn    = findViewById(R.id.diffWrite_Btn);
        multWriteBtn    = findViewById(R.id.multWrite_Btn);
        divWriteBtn     = findViewById(R.id.divWrite_Btn);


        // --------------------------------------------
        // Mixed op btn
        // --------------------------------------------
        resultWriteBtn  = findViewById(R.id.MixedOps_WriteResultBtn);
        resultChooseBtn = findViewById(R.id.MixedOps_ChooseResultBtn);
        quickCountBtn   = findViewById(R.id.quickCountBtn);
        doublingBtn     = findViewById(R.id.doublingBtn);
        orderBtn        = findViewById(R.id.orderBtn);
        randomOpsBtn    = findViewById(R.id.randomOpsBtn);

        home_img        = findViewById(R.id.home_img);


        // --------------------------------------------
        // Games results ref
        // --------------------------------------------
        totalHighScore_tv        = findViewById(R.id.totalHighScore_tv);
        sumWriteScore_tv         = findViewById(R.id.sumWriteScore_tv);
        diffWriteScore_tv        = findViewById(R.id.diffWriteScore_tv);
        multWriteScore_tv        = findViewById(R.id.multWriteScore_tv);
        divWriteScore_tv         = findViewById(R.id.divWriteScore_tv);
        sumChooseScore_tv        = findViewById(R.id.sumChooseScore_tv);
        diffChooseScore_tv       = findViewById(R.id.diffChooseScore_tv);
        multChooseScore_tv       = findViewById(R.id.multChooseScore_tv);
        divChooseScore_tv        = findViewById(R.id.divChooseScore_tv);
        doublingScore_tv         = findViewById(R.id.doublingScore_tv);
        mixedOps_chooseScore_tv = findViewById(R.id.MixedOps_ChooseScore_tv);
        mixedOps_writeScore_tv = findViewById(R.id.MixedOps_WriteScore_tv);
        randomOpsScore_tv        = findViewById(R.id.randomOpsScore_tv);
        quickCountScore_tv       = findViewById(R.id.quickCountScore_tv);
        orderScore_tv            = findViewById(R.id.orderScore_tv);


        // show updated scores
        showGamesScores();


        // activate clicks on answer buttons
        setBtnPressedListener();

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();



    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Set up the button pressed listener and checking answers
     * ---------------------------------------------------------------------------------------------
     */
    private void setBtnPressedListener() {

        sumChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                intent.putExtra(HIGHSCORE,sumChooseHighscore_value);
                startActivity(intent);
            }
        });

        diffChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                intent.putExtra(HIGHSCORE,diffChooseHighscore_value);
                startActivity(intent);
            }
        });


        multChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                intent.putExtra(HIGHSCORE,multChooseHighscore_value);
                startActivity(intent);
            }
        });


        divChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                intent.putExtra(HIGHSCORE,divChooseHighscore_value);
                startActivity(intent);
            }
        });

        sumWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"+");
                intent.putExtra(HIGHSCORE,sumWriteHighscore_value);
                startActivity(intent);
            }
        });


        diffWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"-");
                intent.putExtra(HIGHSCORE,diffWriteHighscore_value);
                startActivity(intent);
            }
        });


        multWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"*");
                intent.putExtra(HIGHSCORE,multWriteHighscore_value);
                startActivity(intent);
            }
        });

        divWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(OPERATION_KEY,"/");
                intent.putExtra(HIGHSCORE,divWriteHighscore_value);
                startActivity(intent);
            }
        });


        resultWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Write_Result_Activity.class);
                intent.putExtra(HIGHSCORE,mixedOps_writeHighscore_value);
                startActivity(intent);
            }
        });

        resultChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, Math_Op_Choose_Result_Activity.class);
                intent.putExtra(HIGHSCORE,mixedOps_chooseHighscore_value);
                startActivity(intent);
            }
        });

        quickCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this,CountObjectsActivity.class);
                intent.putExtra(HIGHSCORE,quickCountHighscore_value);
                startActivity(intent);
            }
        });


        doublingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this,DoubleNumberActivity.class);
                intent.putExtra(HIGHSCORE,doublingHighscore_value);
                startActivity(intent);
            }
        });


        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this,NumberOrderActivity.class);
                intent.putExtra(HIGHSCORE,orderHighscore_value);
                startActivity(intent);
            }
        });

        randomOpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this,RandomOperationActivity.class);
                intent.putExtra(HIGHSCORE,randomOpsHighscore_value);
                startActivity(intent);
            }
        });


        home_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoresActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private void showGamesScores() {
        HighscoresViewModelFactory factory = new HighscoresViewModelFactory();

        // keep updated with LiveData/ViewModel
        HighScoresViewModel viewModel = ViewModelProviders.of(this, factory).get(HighScoresViewModel.class);

        // retrieve data
        LiveData<List<GameResult>> gameResults = viewModel.getGameResultsList();

        // get data from observer, update view
        gameResults.observe(this, new Observer<List<GameResult>>() {
            @Override
            public void onChanged(@Nullable List<GameResult> gameResultEntries) {
                Log.d(TAG, "LiveData : updated game results received. ");
                setGameResults(gameResultEntries);
            }
        });
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Put game result in textview
     * ---------------------------------------------------------------------------------------------
     */
    private void setGameResults(List<GameResult> gameResults){

        this.gameResults = gameResults;

        // set vars
        totalHighScore_value           = MathBrainerUtility.getGameResultsFromList("global_score",gameResults);

        sumWriteHighscore_value        = MathBrainerUtility.getGameResultsFromList("sum_write_result_game_score",gameResults);
        diffWriteHighscore_value       = MathBrainerUtility.getGameResultsFromList("diff_write_result_game_score",gameResults);
        multWriteHighscore_value       = MathBrainerUtility.getGameResultsFromList("mult_write_result_game_score",gameResults);
        divWriteHighscore_value        = MathBrainerUtility.getGameResultsFromList("div_write_result_game_score",gameResults);

        sumChooseHighscore_value       = MathBrainerUtility.getGameResultsFromList("sum_choose_result_game_score",gameResults);
        diffChooseHighscore_value      = MathBrainerUtility.getGameResultsFromList("diff_choose_result_game_score",gameResults);
        multChooseHighscore_value      = MathBrainerUtility.getGameResultsFromList("mult_choose_result_game_score",gameResults);
        divChooseHighscore_value       = MathBrainerUtility.getGameResultsFromList("div_choose_result_game_score",gameResults);

        doublingHighscore_value        = MathBrainerUtility.getGameResultsFromList("doublenumber_game_score",gameResults);
        mixedOps_chooseHighscore_value = MathBrainerUtility.getGameResultsFromList("mix_choose_result_game_score",gameResults);
        mixedOps_writeHighscore_value  = MathBrainerUtility.getGameResultsFromList("mix_write_result_game_score",gameResults);
        randomOpsHighscore_value       = MathBrainerUtility.getGameResultsFromList("random_op_game_score",gameResults);

        quickCountHighscore_value      = MathBrainerUtility.getGameResultsFromList("count_objects_game_score",gameResults);
        orderHighscore_value           = MathBrainerUtility.getGameResultsFromList("number_order_game_score",gameResults);


        // set textviews
        totalHighScore_tv.setText(Integer.toString(totalHighScore_value));

        sumWriteScore_tv.setText(Integer.toString(sumWriteHighscore_value));
        diffWriteScore_tv.setText(Integer.toString(diffWriteHighscore_value));
        multWriteScore_tv.setText(Integer.toString(multWriteHighscore_value));
        divWriteScore_tv.setText(Integer.toString(divWriteHighscore_value));

        sumChooseScore_tv.setText(Integer.toString(sumChooseHighscore_value));
        diffChooseScore_tv.setText(Integer.toString(diffChooseHighscore_value));
        multChooseScore_tv.setText(Integer.toString(multChooseHighscore_value));
        divChooseScore_tv.setText(Integer.toString(divChooseHighscore_value));

        doublingScore_tv.setText(Integer.toString(doublingHighscore_value));
        mixedOps_chooseScore_tv.setText(Integer.toString(mixedOps_chooseHighscore_value));
        mixedOps_writeScore_tv.setText(Integer.toString(mixedOps_writeHighscore_value));
        randomOpsScore_tv.setText(Integer.toString(randomOpsHighscore_value));

        quickCountScore_tv.setText(Integer.toString(quickCountHighscore_value));
        orderScore_tv.setText(Integer.toString(orderHighscore_value));

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private void hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     // hide navigation bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // hide navigation bar
                        // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN; // // hide status bar
        decorView.setSystemUiVisibility(uiOptions);
    }



}
