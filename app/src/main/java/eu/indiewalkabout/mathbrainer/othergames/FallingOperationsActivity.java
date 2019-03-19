package eu.indiewalkabout.mathbrainer.othergames;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;

// TODO
public class FallingOperationsActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falling_operations);


        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(FallingOperationsActivity.this));
    }
}
