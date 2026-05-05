package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model.DoubleNumberConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.GenerateDoubleNumberChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.UpdateDoubleNumberScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.state.DoubleNumberUiState
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.GameSessionTracker
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
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
class DoubleNumberViewModel @Inject constructor(
    private val generateDoubleNumberChallengeUseCase: GenerateDoubleNumberChallengeUseCase,
    private val updateDoubleNumberScoreUseCase: UpdateDoubleNumberScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {
    private val sessionTracker = GameSessionTracker(GameTypes.DOUBLE_NUMBER.id)
    private var operandRangeMin = 1
    private var operandRangeMax = 100

    private var timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
    private var timerJob: Job? = null
    private var challengesCompleted = 0
    private val _uiState = MutableStateFlow(DoubleNumberUiState())
    val uiState: StateFlow<DoubleNumberUiState> = _uiState.asStateFlow()

    fun initialize(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            sessionTracker.loadStats(gameId, fallbackHighScore, getGameStatsUseCase::invoke)
            resetSessionState(fallbackHighScore)
            launchNewChallenge(resetTimer = true)
        }
    }

    fun onDigitPressed(digit: Int) {
        if (_uiState.value.isGameOver) return
        _uiState.update { it.copy(inputValue = (it.inputValue + digit.toString()).take(7)) }
    }

    fun onDeletePressed() {
        if (_uiState.value.isGameOver) return
        _uiState.update { current ->
            val newValue = if (current.inputValue.isNotEmpty()) current.inputValue.dropLast(1) else ""
            current.copy(inputValue = newValue)
        }
    }

    fun onSubmitPressed() {
        val challenge = _uiState.value.challenge ?: return
        val attempt = _uiState.value.inputValue.toIntOrNull() ?: return
        timerJob?.cancel()

        if (attempt == challenge.answer) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onBackPressed() {
        persistScoreIfNeeded()
    }

    private fun resetSessionState(initialHighScore: Int) {
        timerJob?.cancel()
        sessionTracker.beginSession()
        operandRangeMin = 1
        operandRangeMax = 100
        timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
        challengesCompleted = 0
        _uiState.value = DoubleNumberUiState(
            highScore = sessionTracker.sessionHighScoreOr(initialHighScore)
        )
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateDoubleNumberChallengeUseCase(
            DoubleNumberConfig(
                min = operandRangeMin,
                max = operandRangeMax
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                inputValue = "",
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

        if (challengesCompleted >= _uiState.value.challengesPerLevel) {
            challengesCompleted = 0
            promoteLevel()
            updatedTimer = timerLength
        }

        sessionTracker.recordSuccess(newScore, _uiState.value.level)
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = sessionTracker.highScore,
                challengesCompleted = challengesCompleted,
                timeRemaining = updatedTimer,
                totalTime = updatedTimer
            )
        }
        startDelayedChallenge()
    }

    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        sessionTracker.recordFailure(_uiState.value.level)
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE
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
        sessionTracker.recordFailure(_uiState.value.level)
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE,
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

    private fun promoteLevel() {
        _uiState.update { it.copy(level = it.level + 1) }
        sessionTracker.recordProgress(_uiState.value.level)
        operandRangeMin = operandRangeMax
        operandRangeMax = 100 * _uiState.value.level + 50 * (_uiState.value.level - 1)
        timerLength += LEVEL_TIMER_INCREMENT
    }

    private fun onGameOver() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        val finalScore = _uiState.value.score
        val persistRequest = sessionTracker.buildPersistRequest(finalScore, _uiState.value.level) ?: return
        viewModelScope.launch {
            updateGameStatsUseCase(persistRequest.previousStats, persistRequest.updatedStats)
            if (persistRequest.finalScore > 0) {
                updateDoubleNumberScoreUseCase(persistRequest.finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
        private const val LEVEL_TIMER_INCREMENT = 1_000L
    }
}
