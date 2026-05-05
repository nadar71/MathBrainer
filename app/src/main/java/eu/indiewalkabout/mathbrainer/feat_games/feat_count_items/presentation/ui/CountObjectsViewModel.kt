package eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.mathbrainer.core.presentation.state.ChallengeUiState
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.model.CountObjectsConfig
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.use_cases.GenerateCountObjectsChallengeUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.domain.use_cases.UpdateCountObjectsScoreUseCase
import eu.indiewalkabout.mathbrainer.feat_games.feat_count_items.presentation.state.CountObjectsUiState
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
class CountObjectsViewModel @Inject constructor(
    private val generateCountObjectsChallengeUseCase: GenerateCountObjectsChallengeUseCase,
    private val updateCountObjectsScoreUseCase: UpdateCountObjectsScoreUseCase,
    private val getGameStatsUseCase: GetGameStatsUseCase,
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {
    private var currentGameId: String = GameTypes.QUICK_COUNT.id
    private var previousStats: GameStats? = null
    private var highScore: Int = 0
    private var challengesPlayed: Int = 0
    private var challengesWon: Int = 0
    private var challengesLost: Int = 0
    private var lastLevel: Int = 1
    private var maxItemsToCount = INITIAL_MAX_ITEMS
    private var memorizeDuration = CountObjectsUiState.INITIAL_MEMORIZE_DURATION
    private var timerJob: Job? = null
    private var challengesCompleted = 0
    private var isScorePersisted = false // flag to prevent double writes to the DB

    private val _uiState = MutableStateFlow(CountObjectsUiState())
    val uiState: StateFlow<CountObjectsUiState> = _uiState.asStateFlow()

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

    fun onAnswerSelected(answer: Int) {
        val challenge = _uiState.value.challenge ?: return
        if (_uiState.value.isGameOver || _uiState.value.isShowingItems || _uiState.value.showNextButton) return

        if (answer == challenge.itemsToCount) {
            handleSuccess()
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
        memorizeDuration = CountObjectsUiState.INITIAL_MEMORIZE_DURATION
        challengesCompleted = 0
        _uiState.value = CountObjectsUiState(
            highScore = highScore.takeIf { score -> score > 0 }
                ?: initialHighScore.takeIf { score -> score > 0 },
            challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL
        )
    }

    private fun launchNewChallenge(resetTimer: Boolean) {
        val challenge = generateCountObjectsChallengeUseCase(
            CountObjectsConfig(
                maxItemsToCount = maxItemsToCount,
                answerOffset = ANSWER_OFFSET
            )
        )

        _uiState.update {
            it.copy(
                challenge = challenge,
                isShowingItems = true,
                feedback = null,
                showNextButton = false,
                memorizeDurationMs = memorizeDuration,
                challengeId = it.challengeId + 1
            )
        }

        if (resetTimer) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(memorizeDuration)
            _uiState.update { it.copy(isShowingItems = false) }
        }
    }

    private fun handleSuccess() {
        challengesPlayed++
        challengesWon++
        val newChallengesCompleted = _uiState.value.challengesCompleted + 1
        val newScore = _uiState.value.score + SCORE_INCREMENT
        var newLevel = _uiState.value.level
        var updatedMemorizeDuration = memorizeDuration

        // Check if we should level up
        if (newChallengesCompleted >= _uiState.value.challengesPerLevel) {
            newLevel++
            promoteLevel()
            updatedMemorizeDuration = memorizeDuration
        }

        highScore = maxOf(highScore, newScore)
        lastLevel = maxOf(lastLevel, newLevel)
        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = highScore,
                challengesCompleted = if (newChallengesCompleted >= it.challengesPerLevel) 0 else newChallengesCompleted,
                memorizeDurationMs = updatedMemorizeDuration,
                showNextButton = true,
                level = newLevel
            )
        }
        
        // Update the local challengesCompleted for the next level
        if (newChallengesCompleted >= _uiState.value.challengesPerLevel) {
            challengesCompleted = 0
        } else {
            challengesCompleted = newChallengesCompleted
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
        _uiState.update { it.copy(level = it.level + 1) }
        lastLevel = maxOf(lastLevel, _uiState.value.level)
        if (_uiState.value.level < LEVEL_ITEMS_CAP) {
            maxItemsToCount += ITEMS_INCREMENT
        }
        memorizeDuration = (memorizeDuration + MEMORIZE_INCREMENT).coerceAtMost(MAX_MEMORIZE_DURATION)
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
                updateCountObjectsScoreUseCase(finalScore)
            }
        }
    }

    companion object {
        private const val INITIAL_MAX_ITEMS = 6
        private const val ITEMS_INCREMENT = 2
        private const val LEVEL_ITEMS_CAP = 30
        private const val MEMORIZE_INCREMENT = 500L
        private const val MAX_MEMORIZE_DURATION = 5_000L
        private const val SCORE_INCREMENT = 25
        private const val ANSWER_OFFSET = 10
        private const val INITIAL_CHALLENGES_PER_LEVEL = 10
    }
}
