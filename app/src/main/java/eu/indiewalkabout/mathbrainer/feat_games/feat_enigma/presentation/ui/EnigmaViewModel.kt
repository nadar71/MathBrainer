package eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.model.EnigmaConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.GenerateEnigmaChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.domain.use_cases.UpdateEnigmaScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_enigma.presentation.state.EnigmaUiState
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.GetGameStatsUseCase
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.use_cases.UpdateGameStatsUseCase
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
    private val updateEnigmaScoreUseCase: UpdateEnigmaScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private var highScore: Int = 0
    private var challengesPlayed: Int = 0
    private var challengesWon: Int = 0
    private var challengesLost: Int = 0
    private var lastLevel: Int = 1

    private val _gameStats = MutableStateFlow<GameStats?>(null)
    val gameStats: StateFlow<GameStats?> = _gameStats.asStateFlow()

    private var isScorePersisted = false

    private val _uiState = MutableStateFlow(EnigmaUiState())
    val uiState: StateFlow<EnigmaUiState> = _uiState.asStateFlow()

    fun refreshGameStat(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            val stats = getGameStatsUseCase(gameId)
            _gameStats.value = stats
            highScore = stats?.highScore ?: fallbackHighScore
            challengesPlayed = stats?.challengesPlayed ?: 0
            challengesWon = stats?.challengesWon ?: 0
            challengesLost = stats?.challengesLost ?: 0
            lastLevel = stats?.lastLevel?.takeIf { it > 0 } ?: 1
            _uiState.update { it.copy(highScore = highScore.takeIf { score -> score > 0 }) }
        }
    }

    fun startGame() {
        isScorePersisted = false
        _uiState.update { it.copy(highScore = highScore.takeIf { score -> score > 0 }) }
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
            )
        }
    }


    private fun handleSuccess() {
        challengesPlayed++
        challengesWon++
        promoteLevel()
        val newScore = _uiState.value.score + SCORE_INCREMENT

        highScore = maxOf(highScore, newScore)
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = highScore,
            )
        }
        startDelayedChallenge()
    }

    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        challengesPlayed++
        challengesLost++
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        _uiState.update { it.copy(lives = remainingLives, feedback = ChallengeUiState.Feedback.FAILURE) }

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
        _uiState.update { it.copy(level = it.level + 1) }
        lastLevel = maxOf(lastLevel, _uiState.value.level)
    }

    private fun onGameOver() {
        _uiState.update { it.copy(isGameOver = true) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        highScore = maxOf(highScore, finalScore)
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        val updatedStats = GameStats(
            gameId = GameTypes.ENIGMA.id,
            highScore = highScore,
            challengesPlayed = challengesPlayed,
            challengesWon = challengesWon,
            challengesLost = challengesLost,
            lastLevel = lastLevel
        )
        val previousStats = _gameStats.value
        _gameStats.value = updatedStats
        viewModelScope.launch {
            updateGameStatsUseCase(previousStats, updatedStats)
            if (finalScore > 0) {
                updateEnigmaScoreUseCase(finalScore)
            }
        }
    }

    companion object {
        private const val SCORE_INCREMENT = 50
        private const val NEXT_CHALLENGE_DELAY = 1_200L
        private const val MIN_VALUE = 2
        private const val MAX_VALUE_BASE = 18
        private const val LEVEL_VALUE_INCREMENT = 4
    }
}
