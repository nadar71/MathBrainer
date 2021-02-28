package eu.indiewalkabout.mathbrainer.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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
