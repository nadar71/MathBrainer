package eu.indiewalkabout.mathbrainer.statistics;


/**
 * Singletons class for storing and retrieving match data
 *
 */
public class ResultsManager {

    // singleton instantiation
    private static final Object LOCK = new Object();
    private static ResultsManager resultsManagerSingleton;


    public static ResultsManager getInstance() {
        if(resultsManagerSingleton == null){
            synchronized (LOCK){
                resultsManagerSingleton = new ResultsManager();
            }
        }
        return resultsManagerSingleton;
    }


}
