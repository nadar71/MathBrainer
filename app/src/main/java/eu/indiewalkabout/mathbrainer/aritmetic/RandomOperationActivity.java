package eu.indiewalkabout.mathbrainer.aritmetic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;

public class RandomOperationActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_operation);


        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(RandomOperationActivity.this));
    }
}
