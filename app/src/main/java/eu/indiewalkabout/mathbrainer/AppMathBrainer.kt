package eu.indiewalkabout.mathbrainer

import android.app.Application
import android.content.Context
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.mathbrainer.core.unityads.UNITYTAG
import eu.indiewalkabout.mathbrainer.core.unityads.testMode
import eu.indiewalkabout.mathbrainer.core.util.AppExecutors
import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDatabase
import eu.indiewalkabout.mathbrainer.data.repository.MathBrainerRepository

// Class used for access singletons and application context wherever in the app.
class AppMathBrainer : Application(), IUnityAdsInitializationListener {

    companion object {
        private var sContext: Context? = null
        fun getsContext(): Context? {
            return sContext
        }
    }

    // AppExecutors singleton instance
    var appExecutorsInstance: AppExecutors? = null
        private set

    // Singleton db instance
    val database: MathBrainerDatabase?
        get() = MathBrainerDatabase.getsDbInstance(this)

    // Repository singleton instance
    val repository: MathBrainerRepository?
        get() = database?.let { MathBrainerRepository.getInstance(it) }

    override fun onCreate() {
        super.onCreate()
        appExecutorsInstance = AppExecutors.instance
        sContext = applicationContext

        // Initialize Unity SDK:
        UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), testMode, this)
    }

    // unity ads init complete
    override fun onInitializationComplete() {
        Log.v(UNITYTAG, "UnityAds init complete")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        Log.v(UNITYTAG, "UnityAds init FAILED")
    }


}
