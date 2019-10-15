package eu.indiewalkabout.mathbrainer.statistics;

import eu.indiewalkabout.mathbrainer.util.AppExecutors;
import eu.indiewalkabout.mathbrainer.util.SingletonProvider;

public class Results {

    /**
     * ---------------------------------------------------------------------------------------------
     * Init game results in db
     * ---------------------------------------------------------------------------------------------
     */
    public static void initResultsThread(){
        AppExecutors executorsInstance = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
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
        AppExecutors executorsInstance = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
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
        AppExecutors executorsInstance = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
                repository.incrementGameResultByDelta(gameResultsName, delta);
            }
        });
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Update highscore for a game result if last score is greater
     * ---------------------------------------------------------------------------------------------
     */
    public static void updateGameResultHighscoreThread(final String gameResultsName, final int lastScore){
        AppExecutors executorsInstance = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getAppExecutorsInstance();
        executorsInstance.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MathBrainerRepository repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
                repository.updateGameResultHighscore(gameResultsName, lastScore);
            }
        });
    }










}
