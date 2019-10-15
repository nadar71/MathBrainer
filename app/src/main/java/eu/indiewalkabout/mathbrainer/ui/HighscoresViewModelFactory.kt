package eu.indiewalkabout.mathbrainer.ui


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class HighscoresViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    /*
    public HighscoresViewModelFactory(String foodlistType) {
        this.foodlistType = foodlistType;
    }
    */


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HighScoresViewModel() as T
    }

}
