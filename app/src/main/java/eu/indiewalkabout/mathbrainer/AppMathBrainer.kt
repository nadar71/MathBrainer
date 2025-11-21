package eu.indiewalkabout.mathbrainer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Class used for access singletons and application context wherever in the app.
@HiltAndroidApp
class AppMathBrainer : Application()/*, IUnityAdsInitializationListener*/ {

    companion object {
        /*private var sContext: Context? = null
        fun getsContext(): Context? {
            return sContext
        }*/
        lateinit var appContext: Application
    }

    /*// AppExecutors singleton instance
    var appExecutorsInstance: AppExecutors? = null
        private set

    // Singleton db instance
    val database: MathBrainerDatabase?
        get() = MathBrainerDatabase.getDbInstance(this)

    // Repository singleton instance
    val repository: MathBrainerRepository?
        get() = database?.let { MathBrainerRepository.getInstance(it) }*/

    override fun onCreate() {
        super.onCreate()
        appContext = this
        // appExecutorsInstance = AppExecutors.instance
        // sContext = applicationContext

        // Initialize Unity SDK:
        /*UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), testMode, this)*/
    }

    /*// unity ads init complete
    override fun onInitializationComplete() {
        Log.v(UNITYTAG, "UnityAds init complete")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        Log.v(UNITYTAG, "UnityAds init FAILED")
    }*/


}
