package eu.indiewalkabout.mathbrainer.core.notused

// Singletons class for storing and retrieving match data
object ResultsManager {
    // singleton instantiation
    private val LOCK = Any()
    private var resultsManagerSingleton: ResultsManager? = null
    val instance: ResultsManager?
        get() {
            if (resultsManagerSingleton == null) {
                synchronized(LOCK) { resultsManagerSingleton = ResultsManager }
            }
            return resultsManagerSingleton
        }
}
