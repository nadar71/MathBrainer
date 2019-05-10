package eu.indiewalkabout.mathbrainer.ui;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class HighscoresViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    /*
    public HighscoresViewModelFactory(String foodlistType) {
        this.foodlistType = foodlistType;
    }
    */


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HighScoresViewModel();
    }

}
