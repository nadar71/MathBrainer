package eu.indiewalkabout.mathbrainer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.aritmetic.DoubleNumberActivity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.Math_Op_Write_Result_Activity;
import eu.indiewalkabout.mathbrainer.aritmetic.RandomOperationActivity;
import eu.indiewalkabout.mathbrainer.othergames.CountObjectsActivity;
import eu.indiewalkabout.mathbrainer.othergames.NumberOrderActivity;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.statistics.Results;

/**
 * ---------------------------------------------------------------------------------------------
 * Choose the type of game
 * ---------------------------------------------------------------------------------------------
 */
public class HighscoresActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    public static final String OPERATION_KEY = "operation";

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        // init db with results to 0 where needed
        Results.initResultsThread();

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
        // Basic op
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
        // Mixed op
        // --------------------------------------------
        resultWriteBtn  = findViewById(R.id.MixedOps_WriteResultBtn);
        resultChooseBtn = findViewById(R.id.MixedOps_ChooseResultBtn);
        quickCountBtn   = findViewById(R.id.quickCountBtn);
        doublingBtn     = findViewById(R.id.doublingBtn);
        orderBtn        = findViewById(R.id.orderBtn);
        randomOpsBtn    = findViewById(R.id.randomOpsBtn);

        home_img        = findViewById(R.id.home_img);

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