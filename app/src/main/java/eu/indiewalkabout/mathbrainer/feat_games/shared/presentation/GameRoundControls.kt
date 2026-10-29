package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation

import androidx.annotation.StringRes
import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState

fun shouldShowCountAnswers(
    isShowingItems: Boolean,
    feedback: ChallengeUiState.Feedback?,
    isGameOver: Boolean
): Boolean = !isShowingItems && feedback == null && !isGameOver

@StringRes
fun roundActionLabel(feedback: ChallengeUiState.Feedback?): Int =
    if (feedback == ChallengeUiState.Feedback.SUCCESS) {
        R.string.next_str
    } else {
        R.string.quick_count_relaunch
    }
