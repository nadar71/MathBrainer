package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.unityads.bannerListener
import eu.indiewalkabout.mathbrainer.databinding.ActivityFallingOperationsBinding

// TODO : to be implemented
class FallingOperationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFallingOperationsBinding
    private var bottomBanner: BannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_falling_operations)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_falling_operations)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        // binding.mAdView.loadAd(getAdRequest(this@FallingOperationsActivity))

        bottomBanner = BannerView(this, "banner", UnityBannerSize(320, 50))
        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
        binding.bannerLayout.addView(bottomBanner)
    }
}
