package eu.indiewalkabout.mathbrainer.feat_games.shared.presentation

import eu.indiewalkabout.mathbrainer.R
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRoundControlsTest {

    @Test
    fun `quick count answers appear only while waiting for an answer`() {
        assertFalse(shouldShowCountAnswers(isShowingItems = true, feedback = null, isGameOver = false))
        assertTrue(shouldShowCountAnswers(isShowingItems = false, feedback = null, isGameOver = false))
        assertFalse(
            shouldShowCountAnswers(
                isShowingItems = false,
                feedback = ChallengeUiState.Feedback.SUCCESS,
                isGameOver = false
            )
        )
        assertFalse(
            shouldShowCountAnswers(
                isShowingItems = false,
                feedback = ChallengeUiState.Feedback.FAILURE,
                isGameOver = false
            )
        )
    }

    @Test
    fun `successful round offers next while failed round offers retry`() {
        assertEquals(R.string.next_str, roundActionLabel(ChallengeUiState.Feedback.SUCCESS))
        assertEquals(R.string.quick_count_relaunch, roundActionLabel(ChallengeUiState.Feedback.FAILURE))
    }
}
