package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.GenerateNumberOrderChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.UpdateNumberOrderScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.state.NumberOrderUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumberOrderViewModel @Inject constructor(
    private val generateNumberOrderChallengeUseCase: GenerateNumberOrderChallengeUseCase,
    private val updateNumberOrderScoreUseCase: UpdateNumberOrderScoreUseCase
) : ViewModel() {

    private var maxItemsToCount = INITIAL_MAX_ITEMS
    private var memorizeDuration = NumberOrderUiState.INITIAL_MEMORIZE_DURATION
    private var challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
    private var challengesCompleted = 0
    private var timerJob: Job? = null
    private var isScorePersisted = false // flag to prevent double writes to the DB

    private val _uiState = MutableStateFlow(NumberOrderUiState())
    val uiState: StateFlow<NumberOrderUiState> = _uiState.asStateFlow()

    fun startGame(initialHighScore: Int = 0) {
        _uiState.update { 
            it.copy(
                highScore = initialHighScore.takeIf { score -> score > 0 },
                challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL,
                challengesCompleted = 0,
                level = 1,
                score = 0,
                lives = 3,
                isGameOver = false,
                showNextButton = false,
                isMemorizing = false
            ) 
        }
        viewModelScope.launch { launchNewChallenge(resetTimer = true) }
    }

    fun onMarkerTapped(index: Int) {
        val challenge = _uiState.value.challenge ?: return
        if (_uiState.value.isGameOver || _uiState.value.isMemorizing || _uiState.value.showNextButton) return

        val expectedIndex = _uiState.value.revealedCount
        if (index == expectedIndex) {
            val revealed = expectedIndex + 1
            if (revealed >= challenge.itemCount) {
                handleSuccess()
            } else {
                _uiState.update { it.copy(revealedCount = revealed) }
            }
        } else {
            handleFailure()
        }
    }

    fun onNextChallenge() {
        if (_uiState.value.isGameOver) return
        viewModelScope.launch { launchNewChallenge(resetTimer = true) }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateNumberOrderChallengeUseCase(
            NumberOrderConfig(
                maxItemsToCount = maxItemsToCount
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                isMemorizing = true,
                feedback = null,
                showNextButton = false,
                revealedCount = 0,
                challengeId = it.challengeId + 1
            )
        }

        if (resetTimer) {
            startMemorizeTimer()
        }
    }

    private fun startMemorizeTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(memorizeDuration)
            _uiState.update { current ->
                if (!current.isGameOver) current.copy(isMemorizing = false) else current
            }
        }
    }


    private fun handleSuccess() {
        // Increment the local challengesCompleted counter
        challengesCompleted++
        val newScore = _uiState.value.score + SCORE_INCREMENT
        var newLevel = _uiState.value.level

        // Check if we should level up
        val shouldLevelUp = challengesCompleted >= challengesPerLevel
        if (shouldLevelUp) {
            newLevel++
            promoteLevel()
        }

        _uiState.update {
            it.copy(
                feedback = NumberOrderUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(it.highScore ?: 0, newScore),
                revealedCount = it.challenge?.itemCount ?: it.revealedCount,
                challengesCompleted = if (shouldLevelUp) 0 else challengesCompleted,
                showNextButton = true,
                level = newLevel,
                challengesPerLevel = challengesPerLevel // Ensure UI state has the latest challengesPerLevel
            )
        }

        // Reset challengesCompleted if we've leveled up
        if (shouldLevelUp) {
            challengesCompleted = 0
        }
    }



    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = NumberOrderUiState.Feedback.FAILURE,
                showNextButton = remainingLives > 0
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        }
    }

    private fun promoteLevel() {
        _uiState.update {
            it.copy(
                level = it.level + 1,
                challengesCompleted = 0  // Reset challenges completed when leveling up
            )
        }
        if (_uiState.value.level in 2..LEVEL_CAP) {
            maxItemsToCount += ITEMS_INCREMENT
            memorizeDuration = (memorizeDuration + MEMORIZE_INCREMENT).coerceAtMost(MAX_MEMORIZE_DURATION)
            challengesPerLevel += 1
        }
    }

    private fun onGameOver() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGameOver = true, showNextButton = false) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        if (finalScore <= 0) return
        viewModelScope.launch {
            updateNumberOrderScoreUseCase(finalScore)
        }
    }

    companion object {
        private const val INITIAL_MAX_ITEMS = 4f
        private const val ITEMS_INCREMENT = 0.5f
        private const val LEVEL_CAP = 29
        private const val MEMORIZE_INCREMENT = 500L
        private const val MAX_MEMORIZE_DURATION = 5_000L
        private const val SCORE_INCREMENT = 25
        private const val INITIAL_CHALLENGES_PER_LEVEL = 10
    }
}