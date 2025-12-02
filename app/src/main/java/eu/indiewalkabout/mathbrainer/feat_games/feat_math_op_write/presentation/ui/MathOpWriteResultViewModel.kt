package eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.MathWriteConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.model.WriteResultScoreCategory
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.use_cases.GenerateMathWriteChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.domain.usecase.UpdateWriteResultScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_math_op_write.presentation.state.MathWriteUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MathOpWriteResultViewModel @Inject constructor(
    private val generateMathWriteChallengeUseCase: GenerateMathWriteChallengeUseCase,
    private val updateWriteResultScoreUseCase: UpdateWriteResultScoreUseCase
) : ViewModel() {

    // Game state
    private var operationParam: Char? = null
    private var initialHighScore: Int? = null

    private val scoreCategory = WriteResultScoreCategory.fromOperation(operationParam)

    // random range of number to be processed
    private var min = 1
    private var max = 100

    private val multMin = 1
    private var multHighMax = 30
    private var multLowMax = 15

    private val divMin = 1
    private var divHighMax = 15
    private var divLowMax = 11

    private var levelChallengesTarget = 12
    private var levelChallengesCount = 0
    private var timerLength = MathWriteUiState.INITIAL_TIMER_LENGTH

    private var timerJob: Job? = null
    private var scorePersisted = false

    private val _uiState = MutableStateFlow(MathWriteUiState(highScore = initialHighScore))
    val uiState: StateFlow<MathWriteUiState> = _uiState

    fun setOperation(operation: String, highScore: Int = 0) {
        operationParam = operation.firstOrNull()
        initialHighScore = highScore.takeIf { it > 0 }
        viewModelScope.launch {
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
        val symbols = scoreCategory.operationSymbol?.let { listOf(it) } ?: listOf('+', '-', '*', '/')
        val challenge = generateMathWriteChallengeUseCase(
            MathWriteConfig(
                symbols = symbols,
                min = min,
                max = max,
                multMin = multMin,
                multLowMax = multLowMax,
                multHighMax = multHighMax,
                divMin = divMin,
                divLowMax = divLowMax,
                divHighMax = divHighMax
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
        levelChallengesCount++
        val newScore = _uiState.value.score + SCORE_INCREMENT
        var updatedTimer = timerLength

        if (levelChallengesCount > levelChallengesTarget) {
            levelChallengesCount = 0
            promoteLevel()
            updatedTimer = timerLength
        }

        _uiState.update {
            it.copy(
                feedback = MathWriteUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(it.highScore ?: 0, newScore),
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
                feedback = MathWriteUiState.Feedback.FAILURE
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
        _uiState.update { it.copy(lives = remainingLives, feedback = MathWriteUiState.Feedback.FAILURE, timeRemaining = 0L) }
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
        min = max
        max = 100 * _uiState.value.level + 50 * (_uiState.value.level - 1)
        multHighMax += 5
        multLowMax += 1
        divHighMax += 2
        divLowMax += 1
        levelChallengesTarget += 5
        timerLength += 5_000
    }

    private fun onGameOver() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (scorePersisted) return
        scorePersisted = true
        val finalScore = _uiState.value.score
        if (finalScore <= 0) return
        viewModelScope.launch {
            updateWriteResultScoreUseCase(scoreCategory, finalScore)
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 25
        private const val COUNTDOWN_STEP = 1_000L
        private const val NEXT_CHALLENGE_DELAY = 1_000L
    }
}