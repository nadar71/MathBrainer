package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.GenerateEnigmaChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.UpdateEnigmaScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.state.EnigmaUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnigmaViewModel @Inject constructor(
    private val generateEnigmaChallengeUseCase: GenerateEnigmaChallengeUseCase,
    private val updateEnigmaScoreUseCase: UpdateEnigmaScoreUseCase
) : ViewModel() {

    private var challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
    private var challengesCompleted = 0
    private var isScorePersisted = false

    private val _uiState = MutableStateFlow(EnigmaUiState())
    val uiState: StateFlow<EnigmaUiState> = _uiState.asStateFlow()

    fun startGame(initialHighScore: Int = 0) {
        isScorePersisted = false
        _uiState.update { it.copy(highScore = initialHighScore.takeIf { score -> score > 0 }) }
        launchNewChallenge()
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

        if (attempt == challenge.answer) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
    }

    private fun launchNewChallenge() {
        val level = _uiState.value.level
        val challenge = generateEnigmaChallengeUseCase(
            EnigmaConfig(
                level = level,
                minValue = MIN_VALUE,
                maxValue = MAX_VALUE_BASE + level * LEVEL_VALUE_INCREMENT
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                inputValue = "",
                feedback = null,
                challengesPerLevel = challengesPerLevel,
                challengesCompleted = challengesCompleted
            )
        }
    }


    private fun handleSuccess() {
        challengesCompleted++
        val newScore = _uiState.value.score + SCORE_INCREMENT

        if (challengesCompleted >= challengesPerLevel) {
            promoteLevel()
        }

        _uiState.update {
            it.copy(
                feedback = EnigmaUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(it.highScore ?: 0, newScore),
                challengesCompleted = challengesCompleted,
                challengesPerLevel = challengesPerLevel
            )
        }
        startDelayedChallenge()
    }

    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        _uiState.update { it.copy(lives = remainingLives, feedback = EnigmaUiState.Feedback.FAILURE) }

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
                launchNewChallenge()
            }
        }
    }

    private fun promoteLevel() {
        challengesCompleted = 0
        challengesPerLevel += 1
        _uiState.update { it.copy(level = it.level + 1) }
    }

    private fun onGameOver() {
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        viewModelScope.launch {
            if (finalScore > 0) {
                updateEnigmaScoreUseCase(finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 50
        private const val NEXT_CHALLENGE_DELAY = 1_200L
        private const val INITIAL_CHALLENGES_PER_LEVEL = 3
        private const val MIN_VALUE = 2
        private const val MAX_VALUE_BASE = 18
        private const val LEVEL_VALUE_INCREMENT = 4
    }
}
