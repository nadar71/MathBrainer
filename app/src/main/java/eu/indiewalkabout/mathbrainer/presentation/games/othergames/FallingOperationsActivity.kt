package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.ads.AdView
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.mathbrainer.databinding.ActivityFallingOperationsBinding

// TODO : to be implemented
class FallingOperationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFallingOperationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_falling_operations)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_falling_operations)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.mAdView.loadAd(getAdRequest(this@FallingOperationsActivity))
    }
}