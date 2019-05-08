package eu.indiewalkabout.mathbrainer.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import eu.indiewalkabout.mathbrainer.statistics.GameResult;
import eu.indiewalkabout.mathbrainer.statistics.MathBrainerRepository;
import eu.indiewalkabout.mathbrainer.util.SingletonProvider;


/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving games results
 * -------------------------------------------------------------------------------------------------
 */
public class HighScoresViewModel extends ViewModel {

    private static final String TAG = HighScoresViewModel.class.getSimpleName();

    private final LiveData<List<GameResult>> gameResultList;

    private final MathBrainerRepository repository;

    public HighScoresViewModel(){
        Log.d(TAG, "Retrieving game results list from repository");

        // get repository instance
        repository = ((SingletonProvider) SingletonProvider.getsContext()).getRepository();
        gameResultList = repository.getAllGamesResults();
    }


    public LiveData<List<GameResult>> getGameResultsList(){
     return   gameResultList;
    }


}
