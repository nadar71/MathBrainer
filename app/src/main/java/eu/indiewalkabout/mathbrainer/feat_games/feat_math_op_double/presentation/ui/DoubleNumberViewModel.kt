package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.model.DoubleNumberConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.GenerateDoubleNumberChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.domain.use_cases.UpdateDoubleNumberScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_double.presentation.state.DoubleNumberUiState
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.ArithmeticChallengeProgression
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.CountdownChallengeLoop
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.GameSessionTracker
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
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
    private val progression = ArithmeticChallengeProgression(
        initialChallengesPerLevel = DoubleNumberUiState().challengesPerLevel,
        challengesPerLevelIncrement = 0,
        promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.ON_TARGET,
        timerIncrementProvider = { LEVEL_TIMER_INCREMENT }
    )
    private val challengeLoop = CountdownChallengeLoop(viewModelScope, COUNTDOWN_STEP, NEXT_CHALLENGE_DELAY)
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
        challengeLoop.cancelCountdown()

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
        challengeLoop.cancelAll()
        sessionTracker.beginSession()
        progression.reset()
        _uiState.value = DoubleNumberUiState(
            highScore = sessionTracker.sessionHighScoreOr(initialHighScore)
        )
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateDoubleNumberChallengeUseCase(
            DoubleNumberConfig(
                min = progression.operandRangeMin,
                max = progression.operandRangeMax
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                inputValue = "",
                feedback = null,
                timeRemaining = progression.timerLength,
                totalTime = progression.timerLength
            )
        }

        if (resetTimer) {
            startTimer()
        }
    }

    private fun startTimer() {
        challengeLoop.startCountdown(
            durationMs = progression.timerLength,
            onTick = { remaining -> _uiState.update { it.copy(timeRemaining = remaining) } },
            onExpired = ::handleCountdownExpired
        )
    }

    private fun handleSuccess() {
        val newScore = _uiState.value.score + SCORE_INCREMENT
        val progressionUpdate = progression.recordSuccess(_uiState.value.level)
        val nextLevel = progressionUpdate.nextLevel

        if (nextLevel != null) {
            _uiState.update { it.copy(level = nextLevel) }
            sessionTracker.recordProgress(nextLevel)
        }

        val currentLevel = nextLevel ?: _uiState.value.level
        sessionTracker.recordSuccess(newScore, currentLevel)
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = sessionTracker.highScore,
                challengesCompleted = progressionUpdate.challengesCompleted,
                challengesPerLevel = progressionUpdate.challengesPerLevel,
                timeRemaining = progressionUpdate.timerLength,
                totalTime = progressionUpdate.timerLength
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
        challengeLoop.scheduleNextChallenge(
            shouldLaunch = { !_uiState.value.isGameOver },
            onLaunch = { launchNewChallenge(resetTimer = true) }
        )
    }

    private fun onGameOver() {
        challengeLoop.cancelAll()
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
