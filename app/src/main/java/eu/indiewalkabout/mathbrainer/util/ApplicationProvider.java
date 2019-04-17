package eu.indiewalkabout.mathbrainer.util;

import android.app.Application;
import android.content.Context;

import eu.indiewalkabout.mathbrainer.statistics.MathBrainerDatabase;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerRepository;


/**
 * -------------------------------------------------------------------------------------------------
 * Class used for access singletons and application context wherever in the app.
 * Just like repository is an interface for all data operations.
 * NB : register in manifest in <Application  android:name=".ApplicationProvider" >... </Application>
 * -------------------------------------------------------------------------------------------------
 */
public class ApplicationProvider  extends Application {

    private AppExecutors mAppExecutors;

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppExecutors = AppExecutors.getInstance();

        sContext = getApplicationContext();


    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Return singleton db instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public MathBrainerDatabase getDatabase() {
        return MathBrainerDatabase.getsDbInstance(this);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return depository singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public MathBrainerRepository getRepository() {
        return MathBrainerRepository.getInstance(getDatabase());
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return AppExecutors singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public AppExecutors getAppExecutorsInstance() {
        return mAppExecutors;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return application context wherever we are in the app
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static Context getsContext(){
        return sContext;
    }

}
