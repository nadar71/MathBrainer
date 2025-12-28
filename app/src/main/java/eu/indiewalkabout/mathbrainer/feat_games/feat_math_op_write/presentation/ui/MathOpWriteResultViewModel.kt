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
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.state.MathWriteUiState
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
class MathOpWriteResultViewModel @Inject constructor(
    private val generateMathWriteChallengeUseCase: GenerateMathWriteChallengeUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase,
    private val updateWriteResultScoreUseCase: UpdateWriteResultScoreUseCase
) : ViewModel() {

    private var operationParam: String = ""
    private var initialHighScore: Int? = null

    // score category for saving score
    private val scoreCategory: WriteResultScoreCategory
        get() = WriteResultScoreCategory.fromOperation(operationParam)


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

    private var challengesPerLevel: Int = 10 // num. levels to complete before next level
    private var challengesCompleted = 0

    private var timerLength = ChallengeUiState.INITIAL_TIMER_LENGTH
    private var timerJob: Job? = null
    private var isScorePersisted = false // flag to prevent double writes to the DB

    // game state
    private val _uiState = MutableStateFlow(MathWriteUiState(highScore = initialHighScore))
    val uiState: StateFlow<MathWriteUiState> = _uiState.asStateFlow()

    fun setOperation(operation: String, highScore: Int = 0) {
        operationParam = operation
        initialHighScore = highScore.takeIf { it > 0 }
        isScorePersisted = false
        viewModelScope.launch {
            _uiState.update { it.copy(highScore = initialHighScore) }
            launchNewChallenge(resetTimer = true)
        }
    }

    fun onDigitPressed(digit: Int) {
        if (_uiState.value.isGameOver) return
        _uiState.update { it.copy(inputValue = (it.inputValue + digit.toString()).take(7)) }
    }

    fun onDelete() {
        if (_uiState.value.isGameOver) return
        _uiState.update { current ->
            val newValue = if (current.inputValue.isNotEmpty()) current.inputValue.dropLast(1) else ""
            current.copy(inputValue = newValue)
        }
    }

    fun submitAnswer() {
        val challenge = _uiState.value.challenge ?: return
        val attempt = _uiState.value.inputValue.toIntOrNull() ?: return
        timerJob?.cancel()

        if (attempt == challenge.answer) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateMathWriteChallengeUseCase(
            MathWriteConfig(
                operationCode = operationParam,
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

        if (challengesCompleted > challengesPerLevel) {
            challengesCompleted = 0
            promoteLevel()
            updatedTimer = timerLength
        }

        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
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

    // Increases the current level by 1 and updates the game parameters accordingly.
    private fun promoteLevel() {
        _uiState.update { it.copy(level = it.level + 1) }
        operandRangeMin = operandRangeMax
        operandRangeMax = 100 * _uiState.value.level + 50 * (_uiState.value.level - 1)
        multiplicationConfig.maxOperandHigh += 5
        multiplicationConfig.maxOperandLow  += 1
        divisionConfig.maxOperandHigh       += 2
        divisionConfig.maxOperandLow        += 1
        challengesPerLevel += 2
        challengesCompleted = 0
        timerLength += 1_000 * _uiState.value.level
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
                gameId = operationParam,
                sessionScore = finalScore,
                isWin = finalScore > 0,
                lastLevel = _uiState.value.level
            )
            if (finalScore > 0) {
                updateWriteResultScoreUseCase(scoreCategory, finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
    }
}
