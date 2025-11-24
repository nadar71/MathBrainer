package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme


@AndroidEntryPoint
class HomeGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MathBrainerTheme {
                HomeScreen()
            }
        }
    }


    companion object {
        const val OPERATION_KEY = "operation"
        const val HIGHSCORE = "highscore"
    }
}


