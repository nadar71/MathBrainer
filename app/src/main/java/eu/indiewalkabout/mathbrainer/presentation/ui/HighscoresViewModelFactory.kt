package eu.indiewalkabout.mathbrainer.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HighscoresViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HighScoresViewModel() as T
    }
}
