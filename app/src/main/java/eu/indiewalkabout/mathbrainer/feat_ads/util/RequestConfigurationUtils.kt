package eu.indiewalkabout.mathbrainer.feat_ads.util

import eu.indiewalkabout.mathbrainer.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import eu.indiewalkabout.mathbrainer.AppMathBrainer.Companion.TEST_DEVICE_ID


class RequestConfigurationUtils {

    companion object {
        fun setTestDeviceIds() {
            if (!BuildConfig.DEBUG) return
            val testDeviceIds = listOf(TEST_DEVICE_ID)
            val configuration =
                RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }
    }
}
