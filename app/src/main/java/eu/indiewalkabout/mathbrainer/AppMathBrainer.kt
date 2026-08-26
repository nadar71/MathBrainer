package eu.indiewalkabout.mathbrainer

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppMathBrainer : Application() {
    companion object {
        lateinit var appContext: Application // global variables
        var TEST_DEVICE_ID: String = ""
        var canRequestAdsFlag: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        if (BuildConfig.DEBUG) {
            TEST_DEVICE_ID = BuildConfig.ADMOB_TEST_DEVICE_ID
        }
        // init admob ads
        MobileAds.initialize(this) {}
    }
}
