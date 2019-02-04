package eu.indiewalkabout.mathbrainer.othergames;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.aritmetic.MixedOp_Choose_Result_Activity;
import eu.indiewalkabout.mathbrainer.util.ConsentSDK;

public class NumberOrderActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_order);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(NumberOrderActivity.this));
    }
}
