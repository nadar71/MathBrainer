package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.model.OperationConfig
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.WriteResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases.GenerateMathWriteChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases.UpdateWriteResultScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.ArithmeticChallengeProgression
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.CountdownChallengeLoop
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.GameSessionTracker
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.TimedChallengeRoundCoordinator
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.state.MathWriteUiState
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MathOpWriteResultViewModel @Inject constructor(
    private val generateMathWriteChallengeUseCase: GenerateMathWriteChallengeUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateWriteResultScoreUseCase: UpdateWriteResultScoreUseCase
) : ViewModel() {
    private var operationParam: String = ""
    private val sessionTracker = GameSessionTracker("")

    // score category for saving score
    private val scoreCategory: WriteResultScoreCategory
        get() = WriteResultScoreCategory.fromOperation(operationParam)

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
    private val progression = ArithmeticChallengeProgression(
        initialChallengesPerLevel = 10,
        challengesPerLevelIncrement = 2,
        promotionThreshold = ArithmeticChallengeProgression.PromotionThreshold.AFTER_TARGET,
        timerIncrementProvider = { level -> 1_000L * level },
        multiplicationConfig = ArithmeticChallengeProgression.OperationBoundsConfig(
            initialLowMax = multiplicationConfig.maxOperandLow,
            initialHighMax = multiplicationConfig.maxOperandHigh,
            lowIncrement = 1,
            highIncrement = 5
        ),
        divisionConfig = ArithmeticChallengeProgression.OperationBoundsConfig(
            initialLowMax = divisionConfig.maxOperandLow,
            initialHighMax = divisionConfig.maxOperandHigh,
            lowIncrement = 1,
            highIncrement = 2
        )
    )
    private val roundCoordinator = TimedChallengeRoundCoordinator(
        scoreIncrement = SCORE_INCREMENT,
        progression = progression,
        sessionTracker = sessionTracker
    )
    private val challengeLoop = CountdownChallengeLoop(viewModelScope, COUNTDOWN_STEP, NEXT_CHALLENGE_DELAY)
    // game state
    private val _uiState = MutableStateFlow(MathWriteUiState())
    val uiState: StateFlow<MathWriteUiState> = _uiState.asStateFlow()

    fun initialize(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            operationParam = gameId
            sessionTracker.loadStats(gameId, fallbackHighScore, getGameStatsUseCase::invoke)
            resetSessionState()
            launchNewChallenge(resetTimer = true)
        }
    }

    private fun resetSessionState() {
        challengeLoop.cancelAll()
        sessionTracker.beginSession()
        progression.reset()
        _uiState.value = MathWriteUiState(
            highScore = sessionTracker.sessionHighScoreOr()
        )
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

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateMathWriteChallengeUseCase(
            MathWriteConfig(
                operationCode = operationParam,
                min = progression.operandRangeMin,
                max = progression.operandRangeMax,
                multMin = multiplicationConfig.minOperand,
                multLowMax = requireNotNull(progression.currentMultiplicationConfig).lowMax,
                multHighMax = requireNotNull(progression.currentMultiplicationConfig).highMax,
                divMin = divisionConfig.minOperand,
                divLowMax = requireNotNull(progression.currentDivisionConfig).lowMax,
                divHighMax = requireNotNull(progression.currentDivisionConfig).highMax
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
        val result = roundCoordinator.onSuccess(
            currentScore = _uiState.value.score,
            currentLevel = _uiState.value.level
        )
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = result.newScore,
                highScore = result.highScore,
                level = result.level,
                challengesCompleted = result.challengesCompleted,
                challengesPerLevel = result.challengesPerLevel,
                timeRemaining = result.timerLength,
                totalTime = result.timerLength
            )
        }
        startDelayedChallenge()
    }

    private fun handleFailure() {
        val result = roundCoordinator.onFailure(
            currentLives = _uiState.value.lives,
            currentLevel = _uiState.value.level
        )
        _uiState.update {
            it.copy(
                lives = result.remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE
            )
        }

        if (result.isGameOver) {
            onGameOver()
        } else {
            startDelayedChallenge()
        }
    }

    private fun handleCountdownExpired() {
        val result = roundCoordinator.onFailure(
            currentLives = _uiState.value.lives,
            currentLevel = _uiState.value.level
        )
        _uiState.update {
            it.copy(
                lives = result.remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE,
                timeRemaining = 0L
            )
        }
        if (result.isGameOver) {
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
                updateWriteResultScoreUseCase(scoreCategory, persistRequest.finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
    }
}
