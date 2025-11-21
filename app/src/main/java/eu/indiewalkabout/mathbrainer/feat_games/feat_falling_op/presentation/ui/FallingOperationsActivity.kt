package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import eu.indiewalkabout.mathbrainer.R

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