package eu.indiewalkabout.mathbrainer.util

import android.app.Application
import android.content.Context

import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDatabase
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerRepository


/**
 * -------------------------------------------------------------------------------------------------
 * Class used for access singletons and application context wherever in the app.
 * Just like repository is an interface for all data operations.
 * NB : register in manifest in <Application android:name=".SingletonProvider">... </Application>
 * -------------------------------------------------------------------------------------------------
 */
class SingletonProvider : Application() {

    /**
     * ---------------------------------------------------------------------------------------------
     * Return AppExecutors singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    var appExecutorsInstance: AppExecutors? = null
        private set

    /**
     * ---------------------------------------------------------------------------------------------
     * Return singleton db instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val database: MathBrainerDatabase?
        get() = MathBrainerDatabase.getsDbInstance(this)


    /**
     * ---------------------------------------------------------------------------------------------
     * Return depository singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val repository: MathBrainerRepository?
        get() = database?.let { MathBrainerRepository.getInstance(it) }

    override fun onCreate() {
        super.onCreate()
        appExecutorsInstance = AppExecutors.instance

        sContext = applicationContext


    }

    companion object {

        private var sContext: Context? = null


        /**
         * ---------------------------------------------------------------------------------------------
         * Return application context wherever we are in the app
         * @return
         * ---------------------------------------------------------------------------------------------
         */
        fun getsContext(): Context? {
            return sContext
        }
    }

}
