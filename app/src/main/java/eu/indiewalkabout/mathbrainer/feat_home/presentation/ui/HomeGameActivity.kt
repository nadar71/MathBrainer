package eu.indiewalkabout.mathbrainer.feat_home.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.core.presentation.navigation.NavGraph

@AndroidEntryPoint
class HomeGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MathBrainerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }

    companion object {
        const val OPERATION_KEY = "operation"
        const val HIGHSCORE = "highscore"
    }
}


