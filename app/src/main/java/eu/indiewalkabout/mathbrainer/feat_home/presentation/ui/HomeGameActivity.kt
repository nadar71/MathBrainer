package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.BuildConfig
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavGraph
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsCoordinator
import eu.indiewalkabout.mathbrainer.feat_ads.util.AdsState
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentManager
import eu.indiewalkabout.mathbrainer.feat_ads.util.GoogleMobileAdsClient
import eu.indiewalkabout.mathbrainer.feat_ads.util.MobileAdsInitializer
import eu.indiewalkabout.mathbrainer.feat_ads.util.RequestConfigurationUtils

@AndroidEntryPoint
class HomeGameActivity : ComponentActivity() {
    private lateinit var adsCoordinator: AdsCoordinator
    private var adsState by mutableStateOf<AdsState>(AdsState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adsCoordinator = AdsCoordinator(
            consentManager = ConsentManager(),
            mobileAdsInitializer = MobileAdsInitializer(),
            mobileAdsClient = GoogleMobileAdsClient(applicationContext),
            testDeviceIds = RequestConfigurationUtils.testDeviceIds(
                isDebug = BuildConfig.DEBUG,
                configuredId = BuildConfig.ADMOB_TEST_DEVICE_ID
            )
        )

        setContent {
            MathBrainerTheme {
                NavGraph(
                    adsState = adsState,
                    onPrivacyOptionsClick = ::showPrivacyOptions
                )
            }
        }

        adsCoordinator.requestConsent(this, ::updateAdsState)
    }

    private fun showPrivacyOptions() {
        adsCoordinator.showPrivacyOptions(this) { state ->
            updateAdsState(state)
            if (state != AdsState.Loading) {
                Toast.makeText(
                    this,
                    getString(R.string.gdpr_dialog_reset_done),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateAdsState(state: AdsState) {
        adsState = state
    }
}
