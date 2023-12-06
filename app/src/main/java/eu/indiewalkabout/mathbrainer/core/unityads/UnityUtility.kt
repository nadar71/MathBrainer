package eu.indiewalkabout.mathbrainer.core.unityads

import android.app.Activity
import android.util.Log
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import eu.indiewalkabout.mathbrainer.core.util.MathBrainerUtility


const val testMode = false
// Initialize the Unity Banner Ad with a margin
val marginDp = 10 // Set your desired margin in dp
const val UNITYTAG = "UnityAdsExample"


// Listener for banner events:
val bannerListener = object : BannerView.IListener {
    override fun onBannerLoaded(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerLoaded: " + bannerAdView.placementId)
    }

    override fun onBannerFailedToLoad(bannerAdView: BannerView, errorInfo: BannerErrorInfo) {
        Log.e(
            UNITYTAG, "Unity Ads failed to load banner for " + bannerAdView.placementId
                + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage)
        // Note that the BannerErrorInfo object can indicate a no fill (refer to the API documentation).
    }

    override fun onBannerClick(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerClick: " + bannerAdView.placementId)
    }

    override fun onBannerLeftApplication(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerLeftApplication: " + bannerAdView.placementId)
    }

}



// Show unity ads with a defined random frequency
object UnityUtility {
    fun showUnityAdsRandom(activity: Activity) {
        val guess = MathBrainerUtility.randRange_ApiCheck(1, 10)
        if (guess <= 4) {
            // TODO : implement interstitial ads random
            /*if (UnityAds.isReady()) {
                UnityAds.show(activity)
            }*/
        }
    }
}

