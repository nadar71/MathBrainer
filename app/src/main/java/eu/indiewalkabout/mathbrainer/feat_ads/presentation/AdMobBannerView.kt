package eu.indiewalkabout.mathbrainer.feat_ads.presentation
import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsState


@Composable
fun AdMobBannerView(
    adsState: AdsState,
    adUnitId: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    if (adsState != AdsState.Allowed) return

    val context = LocalContext.current

    BoxWithConstraints(modifier = modifier.navigationBarsPadding()) {
        if (maxWidth.value <= 0f) return@BoxWithConstraints

        val adSize = remember(maxWidth) {
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                context,
                maxWidth.value.toInt()
            )
        }

        AndroidView(
            modifier = Modifier.fillMaxWidth().height(adSize.height.dp),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdUnitId(adUnitId)
                    setAdSize(adSize)
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("MathBrainerAds", "Banner loaded")
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.w("MathBrainerAds", "Banner load failed: ${error.domain}/${error.code}: ${error.message}")
                        }
                    }
                    loadAd(AdRequest.Builder().build())
                }
            },
            onRelease = { adView -> adView.destroy() },
            update = { adView ->
                if (adView.adSize != adSize) {
                    adView.setAdSize(adSize)
                    adView.loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
