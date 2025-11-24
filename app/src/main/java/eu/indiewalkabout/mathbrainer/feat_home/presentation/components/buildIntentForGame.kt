package eu.indiewalkabout.mathbrainer.feat_home.presentation.components

import android.content.Context
import android.content.Intent
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameUiModel
import eu.indiewalkabout.mathbrainer.feat_home.presentation.ui.HomeGameActivity

fun buildIntentForGame(gameUiModel: GameUiModel, context: Context): Intent {
    val intent = Intent(context, gameUiModel.definition.target.java)
    gameUiModel.definition.operation?.let { operation ->
        intent.putExtra(HomeGameActivity.Companion.OPERATION_KEY, operation)
    }

    if (gameUiModel.definition.requiresHighScore) {
        gameUiModel.highScore?.let { highScore ->
            intent.putExtra(HomeGameActivity.Companion.HIGHSCORE, highScore)
        }
    }
    return intent
}