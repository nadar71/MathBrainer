package eu.indiewalkabout.mathbrainer.presentation.games.othergames;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.mathbrainer.R;
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK;


// TODO  : to be implemented
public class SoundsSeqActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sounds_seq);

        mAdView = findViewById(R.id.mAdView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.Companion.getAdRequest(SoundsSeqActivity.this));
    }
}
