package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.AppMathBrainer
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavGraph
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_ads.util.ConsentManager
import eu.indiewalkabout.mathbrainer.feat_ads.util.RequestConfigurationUtils

@AndroidEntryPoint
class HomeGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set your test devices.
        RequestConfigurationUtils.setTestDeviceIds()
        ConsentManager.requestConsent(this, this@HomeGameActivity) { canRequestAds ->
            AppMathBrainer.canRequestAdsFlag = canRequestAds
            MobileAds.initialize(this)
            setContent {

                MathBrainerTheme {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}


