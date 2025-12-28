package eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.use_cases.GenerateFallingOperationUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.domain.use_cases.UpdateFallingOpsScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.state.FallingOperationItem
import eu.indiewalkabout.mathbrainer.feat_games.feat_falling_op.presentation.state.FallingOpsUiState
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.random.Random

@HiltViewModel
class FallingOpsViewModel @Inject constructor(
    private val generateFallingOperationUseCase: GenerateFallingOperationUseCase,
    private val updateFallingOpsScoreUseCase: UpdateFallingOpsScoreUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FallingOpsUiState())
    val uiState: StateFlow<FallingOpsUiState> = _uiState.asStateFlow()

    private var fallJob: Job? = null
    private var spawnJob: Job? = null
    private var nextId = 0
    private var speedPerSecond = BASE_SPEED
    private var spawnIntervalMs = BASE_SPAWN_INTERVAL_MS
    private var isScorePersisted = false

    fun startGame(initialHighScore: Int = 0) {
        stopJobs()
        isScorePersisted = false
        nextId = 0
        updateDifficulty(level = 1)
        _uiState.value = FallingOpsUiState(
            highScore = initialHighScore.takeIf { it > 0 }
        )
        addOperation()
        startFallingLoop()
        startSpawnLoop()
    }

    fun onDigitPressed(digit: Int) {
        if (_uiState.value.isGameOver) return
        val currentInput = _uiState.value.input
        if (currentInput.length >= MAX_INPUT_LENGTH) return
        _uiState.update { it.copy(input = currentInput + digit.toString()) }
    }

    fun onDelete() {
        if (_uiState.value.isGameOver) return
        val currentInput = _uiState.value.input
        if (currentInput.isEmpty()) return
        _uiState.update { it.copy(input = currentInput.dropLast(1)) }
    }

    fun onSubmit() {
        if (_uiState.value.isGameOver) return
        val inputValue = _uiState.value.input.toIntOrNull()
        if (inputValue == null) {
            _uiState.update { it.copy(input = "") }
            return
        }

        val matches = _uiState.value.operations.filter { it.definition.result == inputValue }
        if (matches.isEmpty()) {
            _uiState.update {
                it.copy(
                    input = "",
                    feedback = ChallengeUiState.Feedback.FAILURE
                )
            }
            return
        }

        val remaining = _uiState.value.operations.filterNot { it.definition.result == inputValue }
        val removedCount = matches.size
        val updatedScore = _uiState.value.score + removedCount * SCORE_PER_OPERATION
        val updatedExploded = _uiState.value.explodedThisLevel + removedCount
        var updatedLevel = _uiState.value.level
        var updatedTarget = _uiState.value.targetPerLevel
        var explodedThisLevel = updatedExploded
        var updatedOperations = remaining

        if (updatedExploded >= _uiState.value.targetPerLevel) {
            updatedLevel += 1
            updatedTarget = _uiState.value.targetPerLevel + TARGET_INCREMENT
            explodedThisLevel = 0
            updatedOperations = emptyList()
            updateDifficulty(updatedLevel)
        }

        _uiState.update {
            it.copy(
                score = updatedScore,
                highScore = maxOf(it.highScore ?: 0, updatedScore),
                operations = updatedOperations,
                explodedThisLevel = explodedThisLevel,
                level = updatedLevel,
                targetPerLevel = updatedTarget,
                input = "",
                feedback = ChallengeUiState.Feedback.SUCCESS
            )
        }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private fun addOperation() {
        val level = _uiState.value.level
        val maxOperand = min(MAX_OPERAND_CAP, 9 + (level - 1) * OPERAND_STEP)
        val definition = generateFallingOperationUseCase(level, maxOperand)
        val operation = FallingOperationItem(
            id = nextId++,
            definition = definition,
            xPosition = Random.nextFloat().coerceIn(0f, 1f),
            progress = 0f
        )
        _uiState.update { it.copy(operations = it.operations + operation) }
    }

    private fun startFallingLoop() {
        fallJob?.cancel()
        fallJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_MS)
                if (_uiState.value.isGameOver) continue
                updateFallingPositions()
            }
        }
    }

    private fun startSpawnLoop() {
        spawnJob?.cancel()
        spawnJob = viewModelScope.launch {
            while (isActive) {
                delay(spawnIntervalMs)
                if (_uiState.value.isGameOver) continue
                addOperation()
            }
        }
    }

    private fun updateFallingPositions() {
        val updatedOperations = _uiState.value.operations.map { operation ->
            operation.copy(progress = operation.progress + speedPerSecond * (TICK_MS / 1000f))
        }

        if (updatedOperations.any { it.progress >= 1f }) {
            handleLifeLost()
            return
        }

        _uiState.update { it.copy(operations = updatedOperations) }
    }

    private fun handleLifeLost() {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                lives = remainingLives,
                operations = emptyList(),
                input = "",
                feedback = ChallengeUiState.Feedback.FAILURE
            )
        }
        if (remainingLives <= 0) {
            onGameOver()
        }
    }

    private fun onGameOver() {
        stopJobs()
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        viewModelScope.launch {
            updateGameStatsUseCase(
                gameId = GameTypes.FALLING_OPS.id,
                sessionScore = finalScore,
                isWin = finalScore > 0,
                lastLevel = _uiState.value.level
            )
            if (finalScore > 0) {
                updateFallingOpsScoreUseCase(finalScore)
            }
        }
    }

    private fun updateDifficulty(level: Int) {
        speedPerSecond = min(MAX_SPEED, BASE_SPEED + (level - 1) * SPEED_INCREMENT)
        spawnIntervalMs = maxOf(MIN_SPAWN_INTERVAL_MS, BASE_SPAWN_INTERVAL_MS - (level - 1) * SPAWN_INTERVAL_DECREMENT_MS)
    }

    private fun stopJobs() {
        fallJob?.cancel()
        spawnJob?.cancel()
    }

    companion object {
        private const val TICK_MS = 16L
        private const val BASE_SPEED = 0.12f
        private const val SPEED_INCREMENT = 0.03f
        private const val MAX_SPEED = 0.45f

        private const val BASE_SPAWN_INTERVAL_MS = 1_600L
        private const val SPAWN_INTERVAL_DECREMENT_MS = 120L
        private const val MIN_SPAWN_INTERVAL_MS = 700L

        private const val SCORE_PER_OPERATION = 10
        private const val TARGET_INCREMENT = 5
        private const val MAX_OPERAND_CAP = 99
        private const val OPERAND_STEP = 10
        private const val MAX_INPUT_LENGTH = 4
    }
}
