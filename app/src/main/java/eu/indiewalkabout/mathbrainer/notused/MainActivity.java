package eu.indiewalkabout.mathbrainer.notused;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.ui.ChooseGameActivity;
import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;

public class MainActivity extends AppCompatActivity {


    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));
            }
        });



        mAdView = findViewById(R.id.mAdView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));


        // BEGIN Button management

        final Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseGameActivity.class);
                startActivity(intent);
            }
        });

        final Button scoreBtn = findViewById(R.id.scoreBtn);
        scoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        final Button StatBtn = findViewById(R.id.StatBtn);
        StatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        final Button hintsBtn = findViewById(R.id.hintsBtn);
        hintsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ChooseGameActivity.class);
                startActivity(intent);
            }
        });


        // END Button management
    }

    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
