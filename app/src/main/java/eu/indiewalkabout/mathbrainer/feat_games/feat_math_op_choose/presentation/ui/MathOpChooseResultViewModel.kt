package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.model.OperationConfig
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.ChooseResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.model.MathChooseConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases.GenerateMathChooseChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.domain.use_cases.UpdateChooseResultScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_choose.presentation.state.MathChooseUiState
import eu.indiewalkabout.mathbrainer.feat_games.shared.presentation.session.GameSessionTracker
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
class MathOpChooseResultViewModel @Inject constructor(
    private val generateMathChooseChallengeUseCase: GenerateMathChooseChallengeUseCase,
    private val updateChooseResultScoreUseCase: UpdateChooseResultScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private var operationParam: String = ""
    private val sessionTracker = GameSessionTracker("")

    private var scoreCategory = ChooseResultScoreCategory.fromOperation(operationParam)

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

    private var challengesPerLevel: Int = 10
    private var challengesCompleted = 0
    private var optionsCount = MIN_OPTIONS

    private var timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
    private var timerJob: Job? = null
    // game state
    private val _uiState = MutableStateFlow(MathChooseUiState())
    val uiState: StateFlow<MathChooseUiState> = _uiState.asStateFlow()

    fun initialize(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            operationParam = gameId
            sessionTracker.loadStats(gameId, fallbackHighScore, getGameStatsUseCase::invoke)
            resetSessionState(fallbackHighScore)
            launchNewChallenge(resetTimer = true)
        }
    }

    private fun resetSessionState(initialHighScore: Int) {
        scoreCategory = ChooseResultScoreCategory.fromOperation(operationParam)
        timerJob?.cancel()
        sessionTracker.beginSession()
        operandRangeMin = 1
        operandRangeMax = 100
        multiplicationConfig.maxOperandLow = 15
        multiplicationConfig.maxOperandHigh = 30
        divisionConfig.maxOperandLow = 11
        divisionConfig.maxOperandHigh = 15
        challengesPerLevel = 10
        challengesCompleted = 0
        optionsCount = MIN_OPTIONS
        timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
        _uiState.value = MathChooseUiState(
            highScore = sessionTracker.sessionHighScoreOr(initialHighScore)
        )
    }

    fun onOptionSelected(answer: Int) {
        if (_uiState.value.isGameOver) return
        val challenge = _uiState.value.challenge ?: return
        timerJob?.cancel()

        if (answer == challenge.correctAnswer) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onBackPressed() {
        persistScoreIfNeeded()
    }

    private fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateMathChooseChallengeUseCase(
            MathChooseConfig(
                operationCode = operationParam,
                min = operandRangeMin,
                max = operandRangeMax,
                multMin = multiplicationConfig.minOperand,
                multLowMax = multiplicationConfig.maxOperandLow,
                multHighMax = multiplicationConfig.maxOperandHigh,
                divMin = divisionConfig.minOperand,
                divLowMax = divisionConfig.maxOperandLow,
                divHighMax = divisionConfig.maxOperandHigh,
                optionOffset = OPTION_OFFSET
            ),
            optionsCount = optionsCount
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

        sessionTracker.recordSuccess(newScore, _uiState.value.level)
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = sessionTracker.highScore,
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
        _uiState.update { it.copy(lives = remainingLives, feedback = ChallengeUiState.Feedback.FAILURE, timeRemaining = 0L) }
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
        multiplicationConfig.maxOperandHigh += 5
        multiplicationConfig.maxOperandLow += 1
        divisionConfig.maxOperandHigh += 2
        divisionConfig.maxOperandLow += 1
        challengesPerLevel += 2
        challengesCompleted = 0
        timerLength += 1_000 * _uiState.value.level
        optionsCount = (optionsCount + 1).coerceAtMost(MAX_OPTIONS)
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
                updateChooseResultScoreUseCase(scoreCategory, persistRequest.finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
        private const val OPTION_OFFSET = 12
        private const val MIN_OPTIONS = 3
        private const val MAX_OPTIONS = 8
    }
}
