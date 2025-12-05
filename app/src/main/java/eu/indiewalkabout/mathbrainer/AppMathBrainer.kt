package eu.indiewalkabout.mathbrainer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppMathBrainer : Application() {

    companion object {
        lateinit var appContext: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
