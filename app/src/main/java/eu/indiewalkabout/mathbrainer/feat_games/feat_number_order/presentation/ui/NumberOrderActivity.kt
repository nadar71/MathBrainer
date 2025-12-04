package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.mathbrainer.core.presentation.theme.MathBrainerTheme
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeGameActivity

@AndroidEntryPoint
class NumberOrderActivity : ComponentActivity() {

    private val viewModel: NumberOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val highScore = intent?.getIntExtra(HomeGameActivity.HIGHSCORE, 0) ?: 0

        setContent {
            MathBrainerTheme {
                NumberOrderGameScreen(
                    initialHighScore = highScore,
                    onBack = { finish() },
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onDestroy() {
        viewModel.onQuitGame()
        super.onDestroy()
    }
}