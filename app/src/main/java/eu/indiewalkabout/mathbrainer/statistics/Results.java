package eu.indiewalkabout.mathbrainer.statistics;

import android.content.Context;

import eu.indiewalkabout.mathbrainer.util.AppExecutors;
import eu.indiewalkabout.mathbrainer.util.ApplicationProvider;

public class Results {

    /**
     * ---------------------------------------------------------------------------------------------
     * Init game results in db
     * ---------------------------------------------------------------------------------------------
     */
    public static void initResultsThread(){
        AppExecutors executorsInstance = ((ApplicationProvider)ApplicationProvider.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();
                repository.initGameResults();
            }
        });
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Update game results in db
     * ---------------------------------------------------------------------------------------------
     */
    public static void incrementGameResultsThread(final String gameResultsName){
        AppExecutors executorsInstance = ((ApplicationProvider)ApplicationProvider.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();
                repository.incrementGameResult(gameResultsName);
            }
        });
    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Increment results for game which need to be incremented by more then 1 unit,
     * that is by a delta
     * ---------------------------------------------------------------------------------------------
     */
    public static void incrementGameResultByDeltaThread(final String gameResultsName, final int delta){
        AppExecutors executorsInstance = ((ApplicationProvider)ApplicationProvider.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();
                repository.incrementGameResultByDelta(gameResultsName, delta);
            }
        });
    }










}
