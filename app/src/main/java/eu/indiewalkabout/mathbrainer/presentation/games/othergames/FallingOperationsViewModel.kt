package eu.indiewalkabout.mathbrainer.presentation.games.othergames

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.indiewalkabout.mathbrainer.domain.usecase.GenerateFallingOperationUseCase
import eu.indiewalkabout.mathbrainer.presentation.games.othergames.model.FallingOperation

class FallingOperationsViewModel(
    private val generator: GenerateFallingOperationUseCase = GenerateFallingOperationUseCase()
) : ViewModel() {
    private val operations = mutableListOf<FallingOperation>()

    private val _level = MutableLiveData(1)
    val level: LiveData<Int> = _level

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _progressTarget = MutableLiveData(generator.requiredExplosionsForLevel(1))
    val progressTarget: LiveData<Int> = _progressTarget

    private val _operationsState = MutableLiveData<List<FallingOperation>>(emptyList())
    val operationsState: LiveData<List<FallingOperation>> = _operationsState

    private var clearedThisLevel = 0

    fun currentOperations(): List<FallingOperation> = operations

    fun fallSpeed(): Float = generator.fallSpeedForLevel(currentLevel())

    fun spawnIntervalMs(): Long = generator.spawnIntervalForLevel(currentLevel())

    fun addOperation(viewWidth: Int, rectWidth: Float, startOffset: Float) {
        if (viewWidth <= rectWidth) return
        val data = generator.execute(currentLevel())
        val maxX = (viewWidth - rectWidth).toInt().coerceAtLeast(1)
        val x = (0 until maxX).random().toFloat()
        val operation = FallingOperation(
            expression = data.expression,
            result = data.result,
            operator = data.operator,
            x = x,
            y = startOffset
        )
        operations.add(operation)
        _operationsState.value = operations.toList()
    }

    fun advancePositions(rectHeight: Float, viewHeight: Float): Boolean {
        if (operations.isEmpty()) return false
        val delta = fallSpeed()
        var hitBottom = false
        operations.forEach { it.y += delta }
        if (operations.any { it.y + rectHeight >= viewHeight }) {
            hitBottom = true
            operations.clear()
            _operationsState.value = operations.toList()
            loseLife()
        }
        if (!hitBottom) {
            _operationsState.value = operations.toList()
        }
        return hitBottom
    }

    fun consumeInput(inputValue: Int): InputResult {
        val matches = operations.filter { it.result == inputValue }
        if (matches.isEmpty()) {
            return InputResult(false, emptyList(), false)
        }
        operations.removeAll(matches.toSet())
        val newScore = (_score.value ?: 0) + matches.size
        _score.value = newScore
        clearedThisLevel += matches.size
        _progress.value = clearedThisLevel
        _operationsState.value = operations.toList()

        var leveledUp = false
        if (clearedThisLevel >= currentTarget()) {
            levelUp()
            leveledUp = true
        }
        return InputResult(true, matches, leveledUp)
    }

    fun resetInputProgress() {
        _progress.value = clearedThisLevel
    }

    fun resetGame() {
        operations.clear()
        _operationsState.value = operations.toList()
        _level.value = 1
        _score.value = 0
        _lives.value = 3
        clearedThisLevel = 0
        _progress.value = 0
        _progressTarget.value = generator.requiredExplosionsForLevel(1)
    }

    private fun levelUp() {
        operations.clear()
        _operationsState.value = operations.toList()
        clearedThisLevel = 0
        _progress.value = 0
        val nextLevel = currentLevel() + 1
        _level.value = nextLevel
        _progressTarget.value = generator.requiredExplosionsForLevel(nextLevel)
    }

    private fun loseLife() {
        val livesLeft = (currentLives() - 1).coerceAtLeast(0)
        _lives.value = livesLeft
    }

    private fun currentLevel(): Int = _level.value ?: 1

    private fun currentLives(): Int = _lives.value ?: 3

    private fun currentTarget(): Int = _progressTarget.value ?: generator.requiredExplosionsForLevel(currentLevel())

    data class InputResult(
        val matched: Boolean,
        val removedOperations: List<FallingOperation>,
        val leveledUp: Boolean
    )
}
