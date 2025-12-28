package eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.model.MemoryFlashChallenge
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.model.MemoryFlashConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases.GenerateMemoryFlashChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.domain.use_cases.UpdateMemoryFlashScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_memory_flash.presentation.state.MemoryFlashUiState
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class MemoryFlashViewModel @Inject constructor(
    private val generateMemoryFlashChallengeUseCase: GenerateMemoryFlashChallengeUseCase,
    private val updateMemoryFlashScoreUseCase: UpdateMemoryFlashScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private var challengesCompletedInternal = 0
    private var challengesPerLevelInternal = INITIAL_CHALLENGES_PER_LEVEL
    private var isScorePersisted = false
    private var currentChallenge: MemoryFlashChallenge? = null

    private val _uiState = MutableStateFlow(MemoryFlashUiState())
    val uiState: StateFlow<MemoryFlashUiState> = _uiState.asStateFlow()

    fun startGame(initialHighScore: Int = 0) {
        isScorePersisted = false
        challengesCompletedInternal = 0
        challengesPerLevelInternal = INITIAL_CHALLENGES_PER_LEVEL

        _uiState.update {
            it.copy(
                highScore = initialHighScore.takeIf { score -> score > 0 },
                score = 0,
                level = 1,
                lives = 3,
                feedback = null,
                isGameOver = false,
                challengesCompleted = 0,
                challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL,
                revealedSequence = null,
                isReadyForNext = false,
                visibleSequence = "",
                isSequenceVisible = true,
                inputValue = ""
            )
        }

        viewModelScope.launch { launchNewChallenge() }
    }

    fun onDigitPressed(digit: Int) {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext || _uiState.value.isSequenceVisible) return
        _uiState.update { current ->
            current.copy(inputValue = (current.inputValue + digit.toString()).take(12))
        }
    }

    fun onDelete() {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext || _uiState.value.isSequenceVisible) return
        _uiState.update { current ->
            val newValue = if (current.inputValue.isNotEmpty()) current.inputValue.dropLast(1) else ""
            current.copy(inputValue = newValue)
        }
    }

    fun submitAnswer() {
        if (_uiState.value.isGameOver || _uiState.value.isReadyForNext || _uiState.value.isSequenceVisible) return
        val challenge = currentChallenge ?: return
        val attempt = _uiState.value.inputValue

        if (attempt == challenge.answer) {
            handleSuccess(challenge)
        } else {
            handleFailure(challenge)
        }
    }

    fun onNextChallenge() {
        if (_uiState.value.isGameOver || !_uiState.value.isReadyForNext) return
        viewModelScope.launch { launchNewChallenge() }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private fun launchNewChallenge() {
        val config = buildConfig()
        val challenge = generateMemoryFlashChallengeUseCase(config)
        currentChallenge = challenge

        _uiState.update {
            it.copy(
                visibleSequence = challenge.displaySequence,
                inputValue = "",
                feedback = null,
                revealedSequence = null,
                isReadyForNext = false,
                isSequenceVisible = true
            )
        }

        viewModelScope.launch {
            val sequenceLength = challenge.sequence.size
            val revealDuration = calculateRevealDuration(sequenceLength)
            delay(revealDuration)
            _uiState.update { current -> current.copy(isSequenceVisible = false) }
        }
    }

    private fun handleSuccess(challenge: MemoryFlashChallenge) {
        val newScore = _uiState.value.score + challenge.sequence.size * SCORE_PER_DIGIT
        val shouldLevelUp = challengesCompletedInternal + 1 >= challengesPerLevelInternal

        val (nextLevel, nextChallengesPerLevel) = if (shouldLevelUp) {
            challengesCompletedInternal = 0
            promoteLevel(_uiState.value.level)
        } else {
            challengesCompletedInternal++
            _uiState.value.level to challengesPerLevelInternal
        }

        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = max(it.highScore ?: 0, newScore),
                challengesCompleted = challengesCompletedInternal,
                challengesPerLevel = nextChallengesPerLevel,
                level = nextLevel,
                revealedSequence = challenge.displaySequence,
                isReadyForNext = true,
                isSequenceVisible = true
            )
        }
    }

    private fun handleFailure(challenge: MemoryFlashChallenge) {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE,
                revealedSequence = challenge.displaySequence,
                isReadyForNext = remainingLives > 0,
                isSequenceVisible = true
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        }
    }

    private fun promoteLevel(currentLevel: Int): Pair<Int, Int> {
        val nextLevel = currentLevel + 1
        val nextChallengesPerLevel = (INITIAL_CHALLENGES_PER_LEVEL + (nextLevel / 2))
            .coerceAtMost(MAX_CHALLENGES_PER_LEVEL)
        challengesPerLevelInternal = nextChallengesPerLevel
        return nextLevel to nextChallengesPerLevel
    }

    private fun onGameOver() {
        _uiState.update { it.copy(isGameOver = true, isReadyForNext = false) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        viewModelScope.launch {
            updateGameStatsUseCase(
                gameId = GameTypes.MEMORY_FLASH.id,
                sessionScore = finalScore,
                isWin = finalScore > 0,
                lastLevel = _uiState.value.level
            )
            if (finalScore > 0) {
                updateMemoryFlashScoreUseCase(finalScore)
            }
        }
    }

    private fun buildConfig(): MemoryFlashConfig {
        val level = _uiState.value.level
        val length = (BASE_SEQUENCE_LENGTH + level / 2).coerceAtMost(MAX_SEQUENCE_LENGTH)
        val maxDigit = (BASE_MAX_DIGIT + level).coerceAtMost(MAX_DIGIT)

        return MemoryFlashConfig(
            level = level,
            length = length,
            maxDigit = maxDigit
        )
    }

    companion object {
        private const val BASE_SEQUENCE_LENGTH = 3
        private const val MAX_SEQUENCE_LENGTH = 8
        private const val BASE_MAX_DIGIT = 4
        private const val MAX_DIGIT = 9
        private const val SCORE_PER_DIGIT = 10
        private const val BASE_DELAY_PER_DIGIT_MS = 300L  // Base delay per digit in milliseconds
        private const val MIN_TOTAL_DELAY_MS = 900L      // Minimum total delay
        private const val INITIAL_CHALLENGES_PER_LEVEL = 3
        private const val MAX_CHALLENGES_PER_LEVEL = 6
        
        private fun calculateRevealDuration(sequenceLength: Int): Long {
            val calculatedDelay = sequenceLength * BASE_DELAY_PER_DIGIT_MS
            return maxOf(calculatedDelay, MIN_TOTAL_DELAY_MS)
        }
    }
}
