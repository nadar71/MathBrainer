package eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.model.NumberOrderConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.GenerateNumberOrderChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.domain.use_cases.UpdateNumberOrderScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_number_order.presentation.state.NumberOrderUiState
import eu.indiewalkabout.mathbrainer.feat_home.domain.model.GameTypes
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.model.GameStats
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
class NumberOrderViewModel @Inject constructor(
    private val generateNumberOrderChallengeUseCase: GenerateNumberOrderChallengeUseCase,
    private val updateNumberOrderScoreUseCase: UpdateNumberOrderScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private var currentGameId: String = GameTypes.NUMBER_ORDER.id
    private var previousStats: GameStats? = null
    private var highScore: Int = 0
    private var challengesPlayed: Int = 0
    private var challengesWon: Int = 0
    private var challengesLost: Int = 0
    private var lastLevel: Int = 1

    private var maxItemsToCount = INITIAL_MAX_ITEMS
    private var memorizeDuration = NumberOrderUiState.INITIAL_MEMORIZE_DURATION
    private var challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
    private var challengesCompleted = 0
    private var timerJob: Job? = null
    private var isScorePersisted = false // flag to prevent double writes to the DB

    private val _uiState = MutableStateFlow(NumberOrderUiState())
    val uiState: StateFlow<NumberOrderUiState> = _uiState.asStateFlow()

    fun initialize(gameId: String, fallbackHighScore: Int = 0) {
        viewModelScope.launch {
            val stats = getGameStatsUseCase(gameId)
            currentGameId = gameId
            previousStats = stats
            highScore = stats?.highScore ?: fallbackHighScore
            challengesPlayed = stats?.challengesPlayed ?: 0
            challengesWon = stats?.challengesWon ?: 0
            challengesLost = stats?.challengesLost ?: 0
            lastLevel = stats?.lastLevel?.takeIf { it > 0 } ?: 1
            resetSessionState(fallbackHighScore)
            launchNewChallenge(resetTimer = true)
        }
    }

    fun onMarkerTapped(index: Int) {
        val challenge = _uiState.value.challenge ?: return
        if (_uiState.value.isGameOver || _uiState.value.isMemorizing || _uiState.value.showNextButton) return

        val expectedIndex = _uiState.value.revealedCount
        if (index == expectedIndex) {
            val revealed = expectedIndex + 1
            if (revealed >= challenge.itemCount) {
                handleSuccess()
            } else {
                _uiState.update { it.copy(revealedCount = revealed) }
            }
        } else {
            handleFailure()
        }
    }

    fun onNextPressed() {
        if (_uiState.value.isGameOver) return
        viewModelScope.launch { launchNewChallenge(resetTimer = true) }
    }

    fun onBackPressed() {
        persistScoreIfNeeded()
    }

    private fun resetSessionState(initialHighScore: Int) {
        timerJob?.cancel()
        isScorePersisted = false
        maxItemsToCount = INITIAL_MAX_ITEMS
        memorizeDuration = NumberOrderUiState.INITIAL_MEMORIZE_DURATION
        challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
        challengesCompleted = 0
        _uiState.value = NumberOrderUiState(
            highScore = highScore.takeIf { score -> score > 0 }
                ?: initialHighScore.takeIf { score -> score > 0 },
            challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
        )
    }

    private suspend fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateNumberOrderChallengeUseCase(
            NumberOrderConfig(
                maxItemsToCount = maxItemsToCount
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                isMemorizing = true,
                feedback = null,
                showNextButton = false,
                revealedCount = 0,
                challengeId = it.challengeId + 1
            )
        }

        if (resetTimer) {
            startMemorizeTimer()
        }
    }

    private fun startMemorizeTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(memorizeDuration)
            _uiState.update { current ->
                if (!current.isGameOver) current.copy(isMemorizing = false) else current
            }
        }
    }


    private fun handleSuccess() {
        // Increment the local challengesCompleted counter
        challengesCompleted++
        val newScore = _uiState.value.score + SCORE_INCREMENT
        var newLevel = _uiState.value.level

        challengesPlayed++
        challengesWon++

        // Check if we should level up
        val shouldLevelUp = challengesCompleted >= challengesPerLevel
        if (shouldLevelUp) {
            newLevel++
            promoteLevel()
        }

        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(highScore, newScore).also { updated -> highScore = updated },
                revealedCount = it.challenge?.itemCount ?: it.revealedCount,
                challengesCompleted = if (shouldLevelUp) 0 else challengesCompleted,
                showNextButton = true,
                level = newLevel,
                challengesPerLevel = challengesPerLevel // Ensure UI state has the latest challengesPerLevel
            )
        }

        lastLevel = maxOf(lastLevel, newLevel)

        // Reset challengesCompleted if we've leveled up
        if (shouldLevelUp) {
            challengesCompleted = 0
        }
    }



    private fun handleFailure() {
        val remainingLives = _uiState.value.lives - 1
        challengesPlayed++
        challengesLost++
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        _uiState.update {
            it.copy(
                lives = remainingLives,
                feedback = ChallengeUiState.Feedback.FAILURE,
                showNextButton = remainingLives > 0
            )
        }

        if (remainingLives <= 0) {
            onGameOver()
        }
    }

    private fun promoteLevel() {
        _uiState.update {
            it.copy(
                level = it.level + 1,
                challengesCompleted = 0  // Reset challenges completed when leveling up
            )
        }
        if (_uiState.value.level in 2..LEVEL_CAP) {
            maxItemsToCount += ITEMS_INCREMENT
            memorizeDuration = (memorizeDuration + MEMORIZE_INCREMENT).coerceAtMost(MAX_MEMORIZE_DURATION)
            challengesPerLevel += 1
        }
    }

    private fun onGameOver() {
        timerJob?.cancel()
        _uiState.update { it.copy(isGameOver = true, showNextButton = false) }
        persistScoreIfNeeded()
    }

    private fun persistScoreIfNeeded() {
        if (isScorePersisted) return
        isScorePersisted = true
        val finalScore = _uiState.value.score
        highScore = maxOf(highScore, finalScore)
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        val updatedStats = GameStats(
            gameId = currentGameId,
            highScore = highScore,
            challengesPlayed = challengesPlayed,
            challengesWon = challengesWon,
            challengesLost = challengesLost,
            lastLevel = lastLevel
        )
        val existingStats = previousStats
        previousStats = updatedStats
        viewModelScope.launch {
            updateGameStatsUseCase(existingStats, updatedStats)
            if (finalScore > 0) {
                updateNumberOrderScoreUseCase(finalScore)
            }
        }
    }

    companion object {
        private const val INITIAL_MAX_ITEMS = 4f
        private const val ITEMS_INCREMENT = 0.5f
        private const val LEVEL_CAP = 29
        private const val MEMORIZE_INCREMENT = 500L
        private const val MAX_MEMORIZE_DURATION = 5_000L
        private const val SCORE_INCREMENT = 25
        private const val INITIAL_CHALLENGES_PER_LEVEL = 10
    }
}
