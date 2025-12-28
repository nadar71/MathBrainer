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
    private val updateGameStatsUseCase: UpdateGameStatsUseCase
) : ViewModel() {

    private var maxItemsToCount = INITIAL_MAX_ITEMS
    private var memorizeDuration = CountObjectsUiState.INITIAL_MEMORIZE_DURATION
    private var timerJob: Job? = null
    private var challengesCompleted = 0
    private var isScorePersisted = false // flag to prevent double writes to the DB

    private val _uiState = MutableStateFlow(CountObjectsUiState())
    val uiState: StateFlow<CountObjectsUiState> = _uiState.asStateFlow()

    fun startGame(initialHighScore: Int = 0) {
        _uiState.update { 
            it.copy(
                highScore = initialHighScore.takeIf { score -> score > 0 },
                challengesPerLevel = INITIAL_CHALLENGES_PER_LEVEL,
                challengesCompleted = 0,
                level = 1,
                score = 0,
                lives = 3
            ) 
        }
        viewModelScope.launch { launchNewChallenge(resetTimer = true) }
    }

    fun submitAnswer(answer: Int) {
        val challenge = _uiState.value.challenge ?: return
        if (_uiState.value.isGameOver || _uiState.value.isShowingItems || _uiState.value.showNextButton) return

        if (answer == challenge.itemsToCount) {
            handleSuccess()
        } else {
            handleFailure()
        }
    }

    fun onNextChallenge() {
        if (_uiState.value.isGameOver) return
        viewModelScope.launch { launchNewChallenge(resetTimer = true) }
    }

    fun onQuitGame() {
        persistScoreIfNeeded()
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

        _uiState.update {
            it.copy(
                feedback = ChallengeUiState.Feedback.SUCCESS,
                score = newScore,
                highScore = maxOf(it.highScore ?: 0, newScore),
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
        viewModelScope.launch {
            updateGameStatsUseCase(
                gameId = GameTypes.QUICK_COUNT.id,
                sessionScore = finalScore,
                isWin = finalScore > 0,
                lastLevel = _uiState.value.level
            )
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
