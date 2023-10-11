package eu.indiewalkabout.mathbrainer

import android.app.Application
import android.content.Context

import eu.indiewalkabout.mathbrainer.data.local.db.MathBrainerDatabase
import eu.indiewalkabout.mathbrainer.data.repository.MathBrainerRepository
import eu.indiewalkabout.mathbrainer.core.util.AppExecutors


// Class used for access singletons and application context wherever in the app.
class AppMathBrainer : Application() {

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
    }

    companion object {
        private var sContext: Context? = null
        fun getsContext(): Context? {
            return sContext
        }
    }

}
