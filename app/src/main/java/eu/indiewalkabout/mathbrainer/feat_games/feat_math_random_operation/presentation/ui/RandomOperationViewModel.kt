package eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.presentation.ui

import RandomOperationUiState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.model.OperationConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.model.RandomOperationConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases.GenerateRandomOperationChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_random_operation.domain.use_cases.UpdateRandomOperationScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomOperationViewModel @Inject constructor(
    private val generateRandomOperationChallengeUseCase: GenerateRandomOperationChallengeUseCase,
    private val updateRandomOperationScoreUseCase: UpdateRandomOperationScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    // random range of number to be processed
    private var operandRangeMin = 1
    private var operandRangeMax = 100

    // multiplication data range
    private val multiplicationConfig = OperationConfig(
        minOperand = 1,
        maxOperandLow = 15,
        maxOperandHigh = 30
    )

    // division data range
    private val divisionConfig = OperationConfig(
        minOperand = 1,
        maxOperandLow = 11,
        maxOperandHigh = 15
    )

    private var challengesPerLevel: Int = 12
    private var challengesCompleted = 0

    private var timerLength = RandomOperationUiState.INITIAL_TIMER_LENGTH
    private var timerJob: Job? = null
    private var isScorePersisted = false // flag to prevent double writes to the DB

    private val _uiState = MutableStateFlow(RandomOperationUiState())
    val uiState: StateFlow<RandomOperationUiState> = _uiState.asStateFlow()

    fun startGame(initialHighScore: Int = 0) {
        _uiState.update { it.copy(highScore = initialHighScore.takeIf { score -> score > 0 }) }
        viewModelScope.launch {
            launchNewChallenge(resetTimer = true)
        }
    }

    fun onOperationSelected(operation: Char) {
        val challenge = _uiState.value.challenge ?: return
        if (_uiState.value.isGameOver) return

        timerJob?.cancel()
        if (operation == challenge.correctOperation) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateRandomOperationChallengeUseCase(
            RandomOperationConfig(
                min = operandRangeMin,
                max = operandRangeMax,
                multMin = multiplicationConfig.minOperand,
                multLowMax = multiplicationConfig.maxOperandLow,
                multHighMax = multiplicationConfig.maxOperandHigh,
                divMin = divisionConfig.minOperand,
                divLowMax = divisionConfig.maxOperandLow,
                divHighMax = divisionConfig.maxOperandHigh
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                feedback = null,
                timeRemaining = timerLength,
                totalTime = timerLength
            )
        }

        if (resetTimer) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = timerLength
            while (remaining > 0) {
                delay(COUNTDOWN_STEP)
                remaining -= COUNTDOWN_STEP
                _uiState.update { it.copy(timeRemaining = remaining) }
            }
            handleCountdownExpired()
        }
    }

    private fun handleSuccess() {
        challengesCompleted++
        val newScore = _uiState.value.score + SCORE_INCREMENT
        var updatedTimer = timerLength

        if (challengesCompleted > challengesPerLevel) {
            challengesCompleted = 0
            promoteLevel()
            updatedTimer = timerLength
        }

        _uiState.update {
            it.copy(
                feedback = RandomOperationUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(it.highScore ?: 0, newScore),
                challengesCompleted = challengesCompleted,
                challengesPerLevel = challengesPerLevel,
                timeRemaining = updatedTimer,
                totalTime = updatedTimer
            )
        }
        startDelayedChallenge()
    }

    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = RandomOperationUiState.Feedback.FAILURE
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        } else {
            startDelayedChallenge()
        }
    }

    private fun handleCountdownExpired() {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = RandomOperationUiState.Feedback.FAILURE,
                timeRemaining = 0L
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        } else {
            startDelayedChallenge()
        }
    }

    private fun startDelayedChallenge() {
        viewModelScope.launch {
            delay(NEXT_CHALLENGE_DELAY)
            if (!_uiState.value.isGameOver) {
                launchNewChallenge(resetTimer = true)
            }
        }
    }

    // Increases the current level by 1 and updates the game parameters accordingly.
    private fun promoteLevel() {
        _uiState.update { it.copy(level = it.level + 1) }
        val level = _uiState.value.level
        operandRangeMin = operandRangeMax
        operandRangeMax = 100 * level + 50 * (level - 1)
        multiplicationConfig.maxOperandHigh += 5
        multiplicationConfig.maxOperandLow += 1
        divisionConfig.maxOperandHigh += 2
        divisionConfig.maxOperandLow += 1
        challengesPerLevel += 5
        challengesCompleted = 0
        timerLength += 5_000L
    }

    private fun onGameOver() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        viewModelScope.launch {
            updateGameStatsUseCase(
                gameId = GameTypes.RANDOM_OPERATION.id,
                sessionScore = finalScore,
                isWin = finalScore > 0,
                lastLevel = _uiState.value.level
            )
            if (finalScore > 0) {
                updateRandomOperationScoreUseCase(finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
    }
}