package eu.indiewalkabout.mathbrainer.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;
import eu.indiewalkabout.mathbrainer.util.MathBrainerUtility;

public class GameCreditsActivity extends AppCompatActivity {

    public static final String TAG = GameCreditsActivity.class.getName();

    final private GameCreditsActivity.UnityAdsListener unityAdsListener = new GameCreditsActivity.UnityAdsListener();

    TextView  gdprConsent_tv;
    ImageView backhome_img;
    private ConsentSDK consentSDK = null;


    // admob banner ref
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        gdprConsent_tv = findViewById(R.id.gdprConsent_tv);

        backhome_img   = findViewById(R.id.backhome_img);

        // Unity ads init
        UnityAds.initialize(this,getResources().getString(R.string.unityads_key),unityAdsListener);



        // Initialize ConsentSDK
        initConsentSDK(this);

        // Checking the status of the user
        if(ConsentSDK.isUserLocationWithinEea(this)) {
            String choice = ConsentSDK.isConsentPersonalized(this)? "Personalize": "Non-Personalize";
            Log.i(TAG, "onCreate: consent choice : "+choice);

            gdprConsent_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check Consent SDK
                    // Request the consent without callback
                    // consentSDK.requestConsent(null);
                    //To get the result of the consent
                    consentSDK.requestConsent(new ConsentSDK.ConsentStatusCallback() {
                        @Override
                        public void onResult(boolean isRequestLocationInEeaOrUnknown, int isConsentPersonalized) {
                            String choice = "";
                            switch (isConsentPersonalized) {
                                case 0:
                                    choice = "Non-Personalize";
                                    break;
                                case 1:
                                    choice = "Personalized";
                                    break;
                                case -1:
                                    choice = "Error occurred";
                            }
                            Log.i(TAG, "onCreate: consent choice : "+choice);

                            hideStatusNavBars();
                        }

                    });

                }


            });

        } else {
            gdprConsent_tv.setVisibility(View.INVISIBLE);
        }


        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(GameCreditsActivity.this));


        backhome_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show unityads randomic
                MathBrainerUtility.INSTANCE.showUnityAdsRandom(GameCreditsActivity.this);

                Intent intent = new Intent(GameCreditsActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        hideStatusNavBars();

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

    /**
     * -----------------------------------------------------------------------------------------
     * Initialize consent
     * @param context
     * -----------------------------------------------------------------------------------------
     */
    private void initConsentSDK(Context context) {
        // Initialize ConsentSDK
        consentSDK = new ConsentSDK.Builder(context)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // Add your test device id "Remove addTestDeviceId on production!"
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Unity ads listener
     * ---------------------------------------------------------------------------------------------
     */
    private class UnityAdsListener implements IUnityAdsListener {

        @Override
        public void onUnityAdsReady(String s) {

        }

        @Override
        public void onUnityAdsStart(String s) {

        }

        @Override
        public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {


            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    public void onBackPressed() {
        super.onBackPressed();

        // show unityads randomic
        MathBrainerUtility.INSTANCE.showUnityAdsRandom(this);

    }
}
